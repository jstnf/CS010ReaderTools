package dev.justinf.zybooks2elearn;

import dev.justinf.zybooks2elearn.consumer.ElearnGradebookConsumer;
import dev.justinf.zybooks2elearn.consumer.ZybooksGradeReportConsumer;
import dev.justinf.zybooks2elearn.data.ElearnBean;
import dev.justinf.zybooks2elearn.data.ElearnGradebook;
import dev.justinf.zybooks2elearn.data.ZybooksBean;
import dev.justinf.zybooks2elearn.data.ZybooksGradeReport;

import javax.swing.*;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Z2EApp {

    private final AppFrame window;
    private final ZybooksGradeReportConsumer grc;
    private final ElearnGradebookConsumer gc;

    private File zyBooksFile;
    private File eLearnFile;
    private ZybooksGradeReport assignmentReport;
    private ElearnGradebook gradebook;

    public Z2EApp() {
        window = new AppFrame(this);
        grc = new ZybooksGradeReportConsumer(this);
        gc = new ElearnGradebookConsumer(this);

        zyBooksFile = null;
        eLearnFile = null;
        assignmentReport = new ZybooksGradeReport();
        gradebook = new ElearnGradebook();
    }

    public void start() {
        JFrame frame = new JFrame("zyBooks2eLearn");
        frame.setContentPane(window.getPanelMain());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Debugging
        // System.out.println(grc.consume("C:/test2.csv"));
        // System.out.println(gc.consume("C:/test.csv"));

        window.update();
    }

    public void attemptZybooksFileRead(File file) {
        log("Attempting to import zyBooks Report (File: .../" + file.getName() + ")");
        if (grc.consume(file.getPath())) {
            log("Success! Imported data from file.");
            zyBooksFile = file;
            window.update();
            attemptBeanLinking();
        } else {
            log("Import failed. Current data is untouched.");
        }
    }

    public void attemptElearnFileRead(File file) {
        log("Attempting to import eLearn Gradebook (File: .../" + file.getName() + ")");
        if (gc.consume(file.getPath())) {
            log("Success! Imported data from file.");
            eLearnFile = file;
            window.update();
            attemptBeanLinking();
        } else {
            log("Import failed. Current data is untouched.");
        }
    }

    public void attemptBeanLinking() {
        if (eLearnFile != null && zyBooksFile != null) {
            log("Detected valid loaded zyBooks and eLearn data files.");
            log("Attempting to link all eLearn students to zyBooks students.");
            log("This process can take awhile.");

            int successCount = 0;

            List<ElearnBean> unlinkedBeans = new ArrayList<>();

            for (ElearnBean bean : gradebook.getBeans()) {
                boolean found = false;
                for (ZybooksBean bean2 : assignmentReport.getBeans()) {
                    if (bean.getNetIdFormatted().equalsIgnoreCase(bean2.getNetIdFormatted())) {
                        bean.setLinkedZybooksBean(bean2);
                        log("LINKED: " + bean.getName() + "@eLearn (" + bean.getNetIdFormatted() + ") to " + bean2.getLastName() + ", " + bean2.getFirstName() + "@zyBooks (" + bean2.getNetIdFormatted() + ")");
                        found = true;
                        successCount++;
                        break;
                    }
                }
                if (!found) {
                    log("!!ERROR!!: Could not find link for " + bean.getName() + "@eLearn (" + bean.getNetIdFormatted() + ")");
                    unlinkedBeans.add(bean);
                }
            }

            log("Successfully linked " + successCount + " of " + (gradebook.getBeans().size()) + " eLearn students.");
            log("Did not touch " + (assignmentReport.getBeans().size() - successCount) + " of " + assignmentReport.getBeans().size() + " zyBooks students.");
            if (!unlinkedBeans.isEmpty()) {
                log("Unlinked students (requires manual grading!): ");
                unlinkedBeans.forEach(b -> {
                    log(b.getName() + " (" + b.getNetIdFormatted() + ")");
                });
            }
        }
    }

    public void transferAndExport(String zybooksKey, String elearnKey) {
        if (zyBooksFile == null || eLearnFile == null) {
            log("Cannot initiate transfer. Requires valid zyBooks and eLearn files.");
            return;
        }

        log("Attempting transfer of " + zybooksKey + " (zyBooks) to " + elearnKey + "(eLearn).");
        double maxGrade = gradebook.getAssignmentMaxPointValues().get(elearnKey);
        for (ElearnBean bean : gradebook.getBeans()) {
            if (bean.getLinkedZybooksBean() == null) {
                log("!!ERROR!!: No zyBooks student match for " + bean.getNetIdFormatted() + ".");
            } else {
                String zyBooksGrade = bean.getLinkedZybooksBean().getAssignments().get(zybooksKey).stream().findFirst().orElse(null);
                if (zyBooksGrade == null) {
                    log("!!ERROR!!: No zyBooks grade present for " + bean.getNetIdFormatted() + ".");
                    continue;
                }

                // Test if reasonable grade
                double doubleValue;
                try {
                    doubleValue = Double.parseDouble(zyBooksGrade);
                } catch (Exception ignored) {
                    log("!!ERROR!!: zyBooks grade for " + bean.getNetIdFormatted() + " is not a number. (" + zyBooksGrade + ")");
                    continue;
                }

                if (doubleValue > maxGrade) {
                    log("!!WARN!!: zyBooks grade for " + bean.getNetIdFormatted() + " exceeds the eLearn max points. (" + doubleValue + " > " + maxGrade + ")");
                }

                bean.getAssignments().remove(elearnKey);
                bean.getAssignments().put(elearnKey, String.valueOf(doubleValue));
            }
        }

        try {
            long currentTime = System.currentTimeMillis();
            File f = new File("./" + currentTime + ".csv");
            log("Exporting to " + f.getAbsolutePath() + "!");

            // This writing strategy provided by opencsv does not allow
            // proper column ordering - so we will write the file manually.
            /*
            Writer writer = new FileWriter(f);
            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withApplyQuotesToAll(false)
                    .build();
            beanToCsv.write(gradebook.getBeans());
            writer.close();
             */

            // Manual file write begin
            PrintStream ps = new PrintStream(f);

            // Handle header first
            StringBuilder header = new StringBuilder("Student,ID,SIS Login ID,Section");
            // Get list of assignments and append them based on list order
            ElearnBean sampler = gradebook.getBeans().get(0);
            List<String> assignmentNames = new ArrayList<>(sampler.getAssignments().keys());
            if (!assignmentNames.isEmpty()) {
                header.append(",");
                for (int i = 0; i < assignmentNames.size() - 1; i++) {
                    header.append(handleComma(assignmentNames.get(i))).append(",");
                }
                header.append(handleComma(assignmentNames.get(assignmentNames.size() - 1)));
            }
            ps.println(header.toString());

            // Now handle each bean
            gradebook.getBeans().forEach(bean -> {
                StringBuilder sb = new StringBuilder();
                sb.append(handleComma(bean.getName())).append(",");
                sb.append(handleComma(bean.geteLearnId())).append(",");
                sb.append(handleComma(bean.getNetId())).append(",");
                sb.append(handleComma(bean.getSection()));

                if (!bean.getAssignments().isEmpty()) {
                    sb.append(",");
                    for (int i = 0; i < assignmentNames.size() - 1; i++) {
                        sb.append(handleComma(bean.getAssignments()
                                .get(assignmentNames.get(i))
                                .stream()
                                .findFirst()
                                .orElse("")
                        )).append(",");
                    }
                    header.append(handleComma(bean.getAssignments()
                            .get(assignmentNames.get(assignmentNames.size() - 1))
                            .stream()
                            .findFirst()
                            .orElse("")
                    ));
                }
                ps.println(sb.toString());
            });
            ps.close();

            log("Export complete. Please review the file before uploading to eLearn!");
            log("NOTE: If you previously performed grade transfers in the same session, they will be reflected in addition to your current transfer in the exported file!");
            log("NOTE2: To continue progress on the exported file in a new session (after closing this app), load it in the eLearn Gradebook section in the \"Load\" tab.");
        } catch (Exception e) {
            e.printStackTrace();
            log("Export failed. Please see console.");
        }
    }

    private String handleComma(String s) {
        String result = s;
        if (s.contains(",")) {
            result = "\"" + s + "\"";
        }
        return result;
    }

    public void log(Object o) {
        System.out.println(o.toString());
        window.log(o);
    }

    public File getZyBooksFile() {
        return zyBooksFile;
    }

    public File geteLearnFile() {
        return eLearnFile;
    }

    public ZybooksGradeReport getAssignmentReport() {
        return assignmentReport;
    }

    public ElearnGradebook getGradebook() {
        return gradebook;
    }
}