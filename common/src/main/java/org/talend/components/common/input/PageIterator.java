/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.talend.components.common.input;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.common.ConnectorException;

import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Help to get a full iterator from a 'per page' getter.
 * Many REST services (or other lind of sources) that provide array work with per page system
 * example : get?page=1 ... page=20 ...
 * This class simplify client interface for this kind of service by exposing simple iterator.
 * while itering on a page, it load the next, and so on.
 * 
 * @param <T>
 */
@Slf4j
public class PageIterator<T> {

    /**
     * function for getting page.
     * @param <T> : objects type of pages.
     */
    @FunctionalInterface
    public interface PageGetter<T> {

        /**
         * Get Iterator for one page
         * @param pageNumber : page number.
         * @return iterator for page {pageNumber}, null if over the last.
         */
        Iterator<T> find(int pageNumber);
    }

    /** this allow to search for next page while iterating on current. */
    private final ExecutorService exe = Executors.newFixedThreadPool(2);

    /** objects to retreive page */
    private final PageRetriever<T>[] retrievers = new PageRetriever[2];

    /** current page retrieve object */
    private int currentRetriever = 0;

    /** next page to retrieve */
    private final AtomicInteger nextPage;

    /** page getter of called service */
    private final PageGetter<T> getter;

    public PageIterator(PageGetter<T> getter) {
        this.retrievers[0] = new PageRetriever<>();
        this.retrievers[1] = new PageRetriever<>();
        this.getter = getter;

        this.retrievers[0].buildNext(exe, getter, 0);
        this.retrievers[1].buildNext(exe, getter, 1);
        nextPage = new AtomicInteger(2);
    }

    public T next() {
        // get page iterator.
        Iterator<T> c = this.getCurrent().getPageIterator();
        if (c == null) {
            log.info("No more page");
            return null;
        }
        if (!c.hasNext()) {
            // if end page, build and get next page.
            this.getCurrent().buildNext(exe, getter, nextPage.getAndIncrement());
            this.currentRetriever = 1 - this.currentRetriever; // switch retriever page.
            return this.next();
        }
        return c.next();
    }

    private PageRetriever<T> getCurrent() {
        return this.retrievers[this.currentRetriever];
    }

    /**
     * Process to retrieve page.
     * @param <T>
     */
    private static class PageRetriever<T> {

        /**  iterator on current page */
        private Iterator<T> currentPage = null;

        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        /** process to get next page. */
        private CompletableFuture<Iterator<T>> futurePage = null;

        /**
         * Build next page.
         * @param exe : executor for threads.
         * @param getter : functionnal page getter.
         * @param pageNumber : page number to retrieve.
         */
        public void buildNext(Executor exe, PageGetter<T> getter, int pageNumber) {
            log.info("build page {}", pageNumber);
            final Lock wr = this.lock.writeLock();
            this.currentPage = null;
            this.futurePage = CompletableFuture.supplyAsync(() -> {
                wr.lock();
                return getter.find(pageNumber);
            }, exe).whenComplete((Iterator<T> input, Throwable exception) -> {
                wr.unlock();
            });
        }

        public Iterator<T> getPageIterator() {
            this.lock.readLock().lock();
            try {
                if (this.currentPage == null && this.futurePage != null) {
                    log.info("Waiting for page");
                    this.currentPage = this.futurePage.get();
                    log.info("Getting for page");
                }
                return this.currentPage;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("interrupt exception try to get workday page", e);
                throw new ConnectorException("Error with workday", e);
            } catch (ExecutionException e) {
                log.error("execution exception try to get workday page", e);
                throw new ConnectorException("Error with workday", e);
            } finally {
                this.lock.readLock().unlock();
            }
        }
    }

}
