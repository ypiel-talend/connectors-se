/*
 * Copyright (C) 2006-2020 Talend Inc. - www.talend.com
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
package org.talend.components.rabbitmq.source;

import static org.talend.components.rabbitmq.MessageConst.MESSAGE_CONTENT;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.talend.components.rabbitmq.exception.ExchangeDeclareException;
import org.talend.components.rabbitmq.exception.QueueDeclareException;
import org.talend.components.rabbitmq.service.I18nMessage;
import org.talend.components.rabbitmq.service.RabbitMQService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Documentation("Main class for JMSInput records processing")
public class InputSource implements Serializable {

    private final InputMapperConfiguration configuration;

    private final RabbitMQService service;

    private final JsonBuilderFactory jsonBuilderFactory;

    private Connection connection;

    private Channel channel;

    private String exchangeQueueName;

    private final I18nMessage i18n;

    private LinkedBlockingQueue<byte[]> queue;

    public InputSource(@Option final InputMapperConfiguration configuration, final RabbitMQService service,
            final JsonBuilderFactory jsonBuilderFactory, final I18nMessage i18nMessage) {
        this.configuration = configuration;
        this.service = service;
        this.jsonBuilderFactory = jsonBuilderFactory;
        this.i18n = i18nMessage;
        this.queue = new LinkedBlockingQueue<>();
    }

    @PostConstruct
    public void init() throws IOException, TimeoutException, NoSuchAlgorithmException {
        connection = service.getConnection(configuration.getBasicConfig().getConnection());
        channel = service.createChannel(connection);

        switch (configuration.getBasicConfig().getReceiverType()) {
        case QUEUE:
            exchangeQueueName = configuration.getBasicConfig().getQueue();
            try {
                channel.queueDeclare(configuration.getBasicConfig().getQueue(), configuration.getBasicConfig().getDurable(),
                        false, configuration.getBasicConfig().getAutoDelete(), null);
            } catch (IOException e) {
                throw new QueueDeclareException(i18n.errorCantDeclareQueue(), e);
            }
            break;
        case EXCHANGE:
            try {
                channel.exchangeDeclare(configuration.getBasicConfig().getExchange(),
                        configuration.getBasicConfig().getExchangeType().getType());
                exchangeQueueName = channel.queueDeclare().getQueue();
                channel.queueBind(exchangeQueueName, configuration.getBasicConfig().getExchange(),
                        configuration.getBasicConfig().getRoutingKey());
            } catch (IOException e) {
                throw new ExchangeDeclareException(i18n.errorCantDeclareExchange(), e);
            }
            break;
        }

        consume();
    }

    @Producer
    public JsonObject next() throws InterruptedException, IOException {
        if (queue.isEmpty() && channel.messageCount(exchangeQueueName) > 0) {
            return Optional.ofNullable(queue.take()).map(this::convertResponseToString).map(this::buildJSON).orElse(null);
        } else {
            return Optional.ofNullable(queue.poll()).map(this::convertResponseToString).map(this::buildJSON).orElse(null);
        }
    }

    private void consume() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            queue.add(delivery.getBody());
        };

        channel.basicConsume(exchangeQueueName, true, deliverCallback, consumerTag -> {
        });
    }

    private String convertResponseToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private JsonObject buildJSON(String text) {
        JsonObjectBuilder recordBuilder = jsonBuilderFactory.createObjectBuilder();
        recordBuilder.add(MESSAGE_CONTENT, text);
        return recordBuilder.build();
    }

    @PreDestroy
    public void release() {
        service.closeConnection(connection);
    }
}