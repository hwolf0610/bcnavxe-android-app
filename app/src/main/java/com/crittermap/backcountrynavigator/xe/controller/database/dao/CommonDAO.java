package com.crittermap.backcountrynavigator.xe.controller.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.controller.database.BC_AbstractDatabaseHelper;
import com.crittermap.backcountrynavigator.xe.share.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public abstract class CommonDAO<T> implements ICommonDAO<T> {
    private BC_AbstractDatabaseHelper mDbHelper;
    private String mTableName;
    private String mPrimaryColumn;
    private Class<T> mClazz;
    private SQLiteDatabase database;

    protected CommonDAO(BC_AbstractDatabaseHelper dbHelper, String tableName, String primaryColumnName, Class<T> clazz) {
        mDbHelper = dbHelper;
        mTableName = tableName;
        mPrimaryColumn = primaryColumnName;
        mClazz = clazz;
    }

    @Override
    public List<T> findByField(String fieldName, String value) throws IllegalAccessException {
        return retrieve(null, fieldName + " = ?", new String[]{value}, null, null, null, null);
    }

    public String insertOrUpdate(T object) {
        try {
            ContentValues values = new ContentValues();
            database = mDbHelper.getWritableDatabase();
            Field[] fields1 = object.getClass().getDeclaredFields();
            List<Field> fields = new ArrayList<>();

            Collections.addAll(fields, fields1);
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if ("serialVersionUID".equals(fieldName))
                    continue;
                if (fieldName.equals(mPrimaryColumn) && TextUtils.isEmpty((CharSequence) field.get(object))) {
                    values.put(fieldName, UUID.randomUUID().toString());
                    continue;
                }
                Class<?> type = field.getType();
                String datatype = type.getName();
                switch (datatype) {
                    case "long":
                        if (field.get(object) != null) {
                            values.put(fieldName, field.getLong(object));
                        }
                        break;
                    case "int":
                    case "java.lang.Integer":
                        if (field.get(object) != null) {
                            values.put(fieldName, field.getInt(object));
                        }
                        break;
                    case "double":
                    case "java.lang.Double":
                        if (field.get(object) != null) {
                            values.put(fieldName, field.getDouble(object));
                        }
                        break;
                    case "java.lang.String":
                        if (field.get(object) != null) {
                            values.put(fieldName, (String) field.get(object));
                        }
                        break;
                    case "boolean":
                    case "java.lang.Boolean":
                        if (field.get(object) != null) {
                            values.put(fieldName, field.getBoolean(object));
                        }
                        break;
                    case "float":
                    case "java.lang.Float":
                        if (field.get(object) != null) {
                            values.put(fieldName, field.getFloat(object));
                        }
                        break;
                    case "short":
                    case "java.lang.Short":
                        if (field.get(object) != null) {
                            values.put(fieldName, field.getShort(object));
                        }
                        break;
                    case "byte":
                    case "java.lang.Byte":
                        if (field.get(object) != null) {
                            values.put(fieldName, field.getByte(object));
                        }
                        break;
                    case "[B":
                        if (field.get(object) != null) {
                            values.put(fieldName, (byte[]) field.get(object));
                        }
                }
            }
            database.replace(mTableName, "TStamp", values);
            return (String) values.get(mPrimaryColumn);
        } catch (Exception ex) {
            Logger.e("insertOrUpdate error", ex.getMessage(), ex);
            return null;
        } finally {
            if (database != null) {
                database.close();
            }
        }

    }

    private ContentValues getFieldsValues(T object, ArrayList<Field> fields) throws IllegalAccessException {
        ContentValues values = new ContentValues();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if ("serialVersionUID".equals(fieldName) || mPrimaryColumn.equals(fieldName))
                continue;
            Class<?> type = field.getType();
            String datatype = type.getName();
            switch (datatype) {
                case "long":
                    values.put(fieldName, field.getLong(object));
                    break;
                case "int":
                case "java.lang.Integer":
                    values.put(fieldName, field.getInt(object));
                    break;
                case "double":
                case "java.lang.Double":
                    values.put(fieldName, field.getDouble(object));
                    break;
                case "java.lang.String":
                    values.put(fieldName, (String) field.get(object));
                    break;
                case "boolean":
                case "java.lang.Boolean":
                    values.put(fieldName, field.getBoolean(object));
                    break;
                case "float":
                case "java.lang.Float":
                    values.put(fieldName, field.getFloat(object));
                    break;
                case "short":
                case "java.lang.Short":
                    values.put(fieldName, field.getShort(object));
                    break;
                case "byte":
                case "java.lang.Byte":
                    values.put(fieldName, field.getByte(object));
                    break;
            }
        }
        return values;
    }

    @Override
    public List<T> getAll() throws IllegalAccessException {
        return retrieve(null, null, null, null, null, null, null);
    }

    @Override
    public long count() throws IllegalAccessException {
        return getAll().size();
    }

    public List<T> retrieve(String[] columns, String selection, String[] selectionArgs,
                             String groupBy, String having, String orderBy, String limit)
            throws IllegalArgumentException, IllegalAccessException {
        Cursor cursor = null;
        List<T> objects = new ArrayList<T>();
        try {

            database = mDbHelper.getSqLiteDatabase();
            if (limit == null) {
                cursor = database.query(mTableName, columns, selection, selectionArgs, groupBy, having,
                        orderBy);
            } else {
                cursor = database.query(mTableName, columns, selection, selectionArgs, groupBy, having,
                        orderBy, limit);
            }

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    T item;
                    try {
                        item = cursorToObject(cursor, columns);
                        objects.add(item);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                    cursor.moveToNext();
                }
            }
        } catch (SQLException ex) {
            Log.v("SQL Retrieve", ex.getMessage(), ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (database != null)
                database.close();
        }
        return objects;
    }

    private T cursorToObject(Cursor cursor, String[] Columns) throws IllegalArgumentException,
            IllegalAccessException, InstantiationException {

        T newItem = mClazz.newInstance();
        if (Columns == null) {
            cursorNonColumnParams(cursor, newItem);
        } else {
            cursorWithColumnParams(cursor, Columns, newItem);
        }

        return newItem;
    }

    private void cursorWithColumnParams(Cursor cursor, String[] Columns, T newItem) throws IllegalAccessException {
        for (String column : Columns) {
            try {
                Field field = newItem.getClass().getField(column);
                if (field != null) {
                    field.setAccessible(true);
                    setValueToField(cursor, newItem, field, cursor.getColumnIndex(column));
                }

            } catch (NoSuchFieldException ex) {
                Logger.e("DB_ERROR", "There is no field: " + column + "belong to object: "
                        + newItem.getClass().getName());
            } catch (IllegalArgumentException ex) {
                Logger.e("DB_ERROR", "IllegalArgumentException: " + column + "belong to object: "
                        + newItem.getClass().getName());
                throw ex;
            }
        }
    }

    private void cursorNonColumnParams(Cursor cursor, T newItem) throws IllegalAccessException {
        Field[] f1 = newItem.getClass().getDeclaredFields();

        ArrayList<Field> fields = new ArrayList<>();
        Collections.addAll(fields, f1);

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if ("serialVersionUID".equals(fieldName)) continue;
            try {
                setValueToField(cursor, newItem, field, cursor.getColumnIndex(fieldName));
            } catch (IllegalArgumentException ex) {
                Logger.e("DB_ERROR", "Invalid Value for field: " + fieldName + "belong to object: "
                        + newItem.getClass().getName() + " index: " + cursor.getColumnIndex(fieldName));
            }
        }
    }

    private void setValueToField(Cursor cursor, T newItem, Field field, int index) throws IllegalAccessException {
        if (index >= 0) {
            int DataType = cursor.getType(index);
            switch (DataType) {
                case Cursor.FIELD_TYPE_NULL:
                    field.set(newItem, null);
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    field.setDouble(newItem, cursor.getDouble(index));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    if (field.getType().isPrimitive()) {
                        field.setInt(newItem, cursor.getInt(index));
                    } else {
                        field.set(newItem, Integer.toString(cursor.getInt(index)));
                    }
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    field.set(newItem, cursor.getString(index));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    break;
            }
        }
    }

    @Override
    public void deleteAll() {
        database = mDbHelper.getWritableDatabase();
        database.execSQL("delete from " + mTableName);
    }

    @Override
    public T findById(String id) throws IllegalAccessException {
        List<T> objects = findByField(mPrimaryColumn, id);
        return (objects != null && objects.size() > 0) ? objects.get(0) : null;
    }

    @Override
    public int delete(String id) {
        database = mDbHelper.getWritableDatabase();
        return database.delete(mTableName, mPrimaryColumn + "= ?", new String[]{id});
    }
}
