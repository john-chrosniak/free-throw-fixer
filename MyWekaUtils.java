

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;






/**
 *
 * @author mm5gg
 */
public class MyWekaUtils {

    public static void main(String[] args) {
       try {
            //  first get the full features file
	        String[][] csvData = MyWekaUtils.readCSV("features_no_make.csv");           
             int[] features = {0, 1, 2}; //, 3, 4, 5, 6, 7, 8, 9, 10, 11}; // all the features
            double final_acc = 0;
            
                        String arffData = MyWekaUtils.csvToArff(csvData, features);
                        double accuracy = MyWekaUtils.classify(arffData,2);
            
            System.out.println(accuracy);
                        
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public static double classify(String arffData, int option) throws Exception {
		StringReader strReader = new StringReader(arffData);
		Instances instances = new Instances(strReader);
		strReader.close();
		instances.setClassIndex(instances.numAttributes() - 1);
		
		RandomForest classifier;
// 		if(option==1)
// 			classifier = new J48(); // Decision Tree classifier
// 		else if(option==2)			
			classifier = new RandomForest();
// 		else if(option == 3)
// 			classifier = new SMO();  //This is a SVM classifier
// 		else 
// 			return -1;
		
		classifier.buildClassifier(instances); // build classifier
      
//       for(String s : classifier.getOptions())
         
		Evaluation eval = new Evaluation(instances);
		eval.crossValidateModel(classifier, instances, 10, new Random(1), new Object[] { });
		
      System.out.println(classifier.getMaxDepth());
		   System.out.println(classifier.getNumFeatures());
         
		return eval.pctCorrect();
	}
    
    
    public static String[][] readCSV(String filePath) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        ArrayList<String> lines = new ArrayList();
        String line;

        while ((line = br.readLine()) != null) {
            lines.add(line);;
        }


        if (lines.size() == 0) {
            System.out.println("No data found");
            return null;
        }

        int lineCount = lines.size();

        String[][] csvData = new String[lineCount][];
        String[] vals;
        int i, j;
        for (i = 0; i < lineCount; i++) {            
                csvData[i] = lines.get(i).split(",");            
        }
        
        return csvData;

    }

    public static String csvToArff(String[][] csvData, int[] featureIndices) throws Exception {
        int total_rows = csvData.length;
        int total_cols = csvData[0].length;
        int fCount = featureIndices.length;
        String[] attributeList = new String[fCount + 1];
        int i, j;
        for (i = 0; i < fCount; i++) {
            attributeList[i] = csvData[0][featureIndices[i]];
        }
        attributeList[i] = csvData[0][total_cols - 1];

        String[] classList = new String[1];
        classList[0] = csvData[1][total_cols - 1];

        for (i = 1; i < total_rows; i++) {
            classList = addClass(classList, csvData[i][total_cols - 1]);
        }

        StringBuilder sb = getArffHeader(attributeList, classList);

        for (i = 1; i < total_rows; i++) {
            for (j = 0; j < fCount; j++) {
                sb.append(csvData[i][featureIndices[j]]);
                sb.append(",");
            }            
            sb.append(csvData[i][total_cols - 1]);
            sb.append("\n");
        }

        return sb.toString();
    }

    private static StringBuilder getArffHeader(String[] attributeList, String[] classList) {
        StringBuilder s = new StringBuilder();
        s.append("@RELATION wada\n\n");

        int i;
        for (i = 0; i < attributeList.length - 1; i++) {
            s.append("@ATTRIBUTE ");
            s.append(attributeList[i]);
            s.append(" numeric\n");
        }

        s.append("@ATTRIBUTE ");
        s.append(attributeList[i]);
        s.append(" {");
        s.append(classList[0]);

        for (i = 1; i < classList.length; i++) {
            s.append(",");
            s.append(classList[i]);
        }
        s.append("}\n\n");
        s.append("@DATA\n");
        return s;
    }

    private static String[] addClass(String[] classList, String className) {
        int len = classList.length;
        int i;
        for (i = 0; i < len; i++) {
            if (className.equals(classList[i])) {
                return classList;
            }
        }

        String[] newList = new String[len + 1];
        for (i = 0; i < len; i++) {
            newList[i] = classList[i];
        }
        newList[i] = className;

        return newList;
    }
}

