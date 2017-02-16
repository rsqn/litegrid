package com.mac.litegrid.clustering.communication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 6/11/13
 *
 * To change this template use File | Settings | File Templates.
 */
public class SomeObject {
    private String name;
    private List<String> values = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void addValue(String v) {
        values.add(v);
    }

    @Override
    public String toString() {
        return "SomeObject{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }
}
