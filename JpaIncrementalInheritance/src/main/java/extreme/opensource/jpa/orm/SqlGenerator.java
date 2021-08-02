/*
 * The MIT License
 *
 * Copyright 2021 Omar Rashwan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package extreme.opensource.jpa.orm;

import extreme.opensource.jpa.annotations.IncrementalEntity;
import java.util.HashMap;
import java.util.Map;
import extreme.opensource.jpa.annotations.IncrementalInheritance;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Table;

/**
 *
 * @author Omar Rashwan
 * @date : 02-08-2021 12:00 AM
 */
public class SqlGenerator {

    private final Map<String, List<Class>> incrementals = new HashMap<>();

    public String GenerateSQL(Class clz) {
        StringBuilder sb = new StringBuilder();

        if (this.isIncrementalGroup(clz)) {
            this.appendToIncrementals(clz);
        } else {
            //do normal sql generation.
        }
        return sb.toString();
    }

    public boolean isIncrementalGroup(Class clz) {

        return clz.isAnnotationPresent(IncrementalInheritance.class)
                || clz.isAnnotationPresent(IncrementalEntity.class);
    }

    public boolean isSuperIncremental(Class clz) {
        return clz.isAnnotationPresent(IncrementalInheritance.class);
    }

    public boolean isSubIncremental(Class clz) {
        return clz.isAnnotationPresent(IncrementalEntity.class);
    }

    public void appendToIncrementals(Class clz) {
        String key = clz.getName();
        if (this.isSubIncremental(clz)) {
            //for simplicity we check only one level of inheritance
            Class sup = clz.getSuperclass();
            key = sup.getName();

        }

        if (!this.incrementals.containsKey(key)) {
            this.incrementals.put(key, new ArrayList<>());
        }
        this.incrementals.get(key).add(clz);
    }

    public String generateDDLSQL() throws Exception {
        StringBuilder sb = new StringBuilder();
        List<Field> allFields = new ArrayList<>();
        String tableName = "";
        for (String key : this.incrementals.keySet()) {
            List<Class> clsz = this.incrementals.get(key);
            for (Class clz : clsz) {
                if (clz.isAnnotationPresent(Table.class)) {
                    Annotation a = clz.getAnnotation(Table.class);
                    Class t = clz.getAnnotation(Table.class).annotationType();
                    Method name = t.getMethod("name");
                    tableName = (String) name.invoke(a);
                }
                allFields.addAll(Arrays.asList(clz.getDeclaredFields()));

            }
        }
        int size = allFields.size();
        int index = 0;
        sb.append("create table ").append(tableName).append(" (");
        for (Field f : allFields) {
            sb.append(f.getName()).append(" varchar(20)").append(index < size - 1 ? ", " : " ); ");

            index++;
        }
        return sb.toString().replaceAll("@tablename", tableName);
    }

    public String generateSelect(Class clz) throws Exception {
        List<Field> selectionFields = new ArrayList<>();
        String tableName = "";
        StringBuffer sb = new StringBuffer();

        if (this.isSubIncremental(clz)) {

            Annotation a = clz.getSuperclass().getAnnotation(Table.class);
            Class t = clz.getSuperclass().getAnnotation(Table.class).annotationType();
            Method name = t.getMethod("name");
            tableName = (String) name.invoke(a);

            selectionFields.addAll(Arrays.asList(clz.getSuperclass().getDeclaredFields()));
        } else {
            Annotation a = clz.getAnnotation(Table.class);
            Class t = clz.getAnnotation(Table.class).annotationType();
            Method name = t.getMethod("name");
            tableName = (String) name.invoke(a);
        }
        selectionFields.addAll(Arrays.asList(clz.getDeclaredFields()));
        sb.append("select ( ");
        int size = selectionFields.size();
        int index = 0;
        for (Field f : selectionFields) {
            sb.append(f.getName()).append(index < size - 1 ? ", " : " ) ");
            index++;
        }
        sb.append(" from ").append(tableName).append(";");
        return sb.toString();
    }
}
