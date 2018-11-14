package org.talend.components.fileio.output;

import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema.Type;

public enum ValueType {
	
	RECORD {
		@Override
		public Object getValue(String column, Record record) {
			return record.getRecord(column);
		}
	},
    ARRAY {
		@Override
		public Object getValue(String column, Record record) {
			return record.getArray(Object.class, column);
		}
	},
    STRING {
		@Override
		public Object getValue(String column, Record record) {
			return record.getString(column);
		}
	},
    BYTES {
		@Override
		public Object getValue(String column, Record record) {
			return record.getBytes(column);
		}
	},
    INT {
		@Override
		public Object getValue(String column, Record record) {
			return record.getInt(column);
		}
	},
    LONG {
		@Override
		public Object getValue(String column, Record record) {
			return record.getLong(column);
		}
	},
    FLOAT {
		@Override
		public Object getValue(String column, Record record) {
			return record.getFloat(column);
		}
	},
    DOUBLE {
		@Override
		public Object getValue(String column, Record record) {
			return record.getDouble(column);
		}
	},
    BOOLEAN {
		@Override
		public Object getValue(String column, Record record) {
			return record.getBoolean(column);
		}
	},
    DATETIME {
		@Override
		public Object getValue(String column, Record record) {
			return record.getDateTime(column);
		}
	};
	
	public abstract Object getValue(String column, Record record);
	
	public static Object getValue(Type type, String column, Record record) {
		return valueOf(type.name()).getValue(column, record);
	}
    
}