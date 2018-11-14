package org.talend.components.fileio.output.csv;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.talend.components.fileio.common.csv.CsvFormatFactory;
import org.talend.components.fileio.configuration.CsvConfiguration;
import org.talend.components.fileio.configuration.EncodingType;
import org.talend.components.fileio.output.RecordFormatter;
import org.talend.components.fileio.output.ValueType;
import org.talend.sdk.component.api.record.Record;

public class CsvRecordFormatter implements RecordFormatter<String> {

	private final CSVFormat format;
	
	private final CsvConfiguration config;
	
	private final String encoding;
	
	public CsvRecordFormatter(final CsvConfiguration dataset) {
		this.format = CsvFormatFactory.createFormat(dataset);
		this.config = dataset;
		if(config.getEncoding4CSV() == EncodingType.OTHER) {
			this.encoding = config.getSpecificEncoding4CSV();
		} else {
			this.encoding = config.getEncoding4CSV().getEncoding();
		}
	}
	
	@Override
	public String formatRecord(Record record) {
		Object[] values = getValues(record);
		if(values == null || values.length == 0) {
			return null;
		}
		System.out.println(Arrays.toString(values));
		return format.format(values);
	}
	
	private Object[] getValues(final Record record) {
		if (record.getSchema().getEntries().isEmpty()) {
            return null;
        }
		return record.getSchema().getEntries().stream().map(c -> ValueType.valueOf(c.getType().name()).getValue(c.getName(), record))
				.map(s -> {
					try {
						if(s == null) {
							return null;
						}
						return new String(String.valueOf(s).getBytes(encoding));
					} catch (UnsupportedEncodingException e) {
						throw new IllegalArgumentException("Unknown encoding " + encoding);
					}
				}).collect(Collectors.toList()).toArray(new Object[0]);
	}

}
