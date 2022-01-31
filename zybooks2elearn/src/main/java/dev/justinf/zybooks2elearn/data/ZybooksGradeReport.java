package dev.justinf.zybooks2elearn.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ZybooksGradeReport {

    private List<String> assignments;
    private List<ZybooksBean> beans;

    public ZybooksGradeReport() {
        assignments = null;
        beans = null;
    }

    public void importFromConsumer(List<ZybooksBean> beans) {
        if (beans.isEmpty()) return;

        assignments = new ArrayList<>();
        this.beans = beans;
        ZybooksBean sampler = beans.get(0);
        assignments.addAll(sampler.getAssignments().keySet());
        assignments.sort(Comparator.naturalOrder());
    }

    public List<String> getAssignments() {
        return assignments;
    }

    public List<ZybooksBean> getBeans() {
        return beans;
    }
}