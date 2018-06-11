package org.talend.components.bigquery;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.StreamSupport;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;

import com.google.api.services.bigquery.BigqueryScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.DatasetId;

@Service
public class BigQueryService {

    @Suggestions("BigQueryDataSet")
    public SuggestionValues findDataSets(@Option("accountFile") final String accountFile,
            @Option("project") final String project) {
        final BigQuery client = createClient(accountFile, project);
        return new SuggestionValues(true, StreamSupport
                .stream(client.listDatasets(project, BigQuery.DatasetListOption.pageSize(100)).getValues().spliterator(), false)
                .map(dataset -> new SuggestionValues.Item(dataset.getDatasetId().getDataset(),
                        ofNullable(dataset.getFriendlyName()).orElseGet(() -> dataset.getDatasetId().getDataset())))
                .collect(toList()));
    }

    @Suggestions("BigQueryTables")
    public SuggestionValues findTables(@Option("accountFile") final String accountFile, @Option("project") final String project,
            @Option("datasetId") final String datasetId) {
        final BigQuery client = createClient(accountFile, project);
        return new SuggestionValues(true, StreamSupport
                .stream(client.listTables(DatasetId.of(project, datasetId), BigQuery.TableListOption.pageSize(100)).getValues()
                        .spliterator(), false)
                .map(table -> new SuggestionValues.Item(table.getTableId().getTable(),
                        ofNullable(table.getFriendlyName()).orElseGet(() -> table.getTableId().getTable())))
                .collect(toList()));
    }

    private BigQuery createClient(final String accountFile, final String project) {
        if (accountFile == null || accountFile.trim().isEmpty()) {
            return BigQueryOptions.getDefaultInstance().getService();
        }
        try {
            return BigQueryOptions.newBuilder().setProjectId(project)
                    .setCredentials(
                            GoogleCredentials.fromStream(new FileInputStream(accountFile)).createScoped(BigqueryScopes.all()))
                    .build().getService();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
