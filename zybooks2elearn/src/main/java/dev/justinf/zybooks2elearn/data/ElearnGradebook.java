package dev.justinf.zybooks2elearn.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElearnGradebook {

    private List<String> assignments;
    private Map<String, Double> assignmentMaxPointValues;
    private List<ElearnBean> beans;

    public ElearnGradebook() {
        assignments = null;
        assignmentMaxPointValues = null;
        beans = null;
    }

    public void importFromConsumer(List<ElearnBean> beans) {
        // In a standard eLearn gradebook, the first two lines after the header
        // Are a filler "Manual Posting" line and a "Points Possible" Line
        if (beans.isEmpty() || beans.size() < 2) return;

        assignments = new ArrayList<>();
        assignmentMaxPointValues = new HashMap<>();
        this.beans = beans;

        ElearnBean sampler = beans.get(1); // Should contain max point values
        sampler.getAssignments().entries().forEach(pair -> {
            String title = pair.getKey();
            double maxPoints = -1;
            try {
                maxPoints = Double.parseDouble(pair.getValue());
            } catch (Exception ignored) { }
            assignments.add(title);
            assignmentMaxPointValues.put(title, maxPoints);
        });
        assignments.sort(Comparator.naturalOrder());
    }

    public List<String> getAssignments() {
        return assignments;
    }

    public Map<String, Double> getAssignmentMaxPointValues() {
        return assignmentMaxPointValues;
    }

    public List<ElearnBean> getBeans() {
        return beans;
    }
}