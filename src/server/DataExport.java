package server;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class DataExport {
	public static boolean exportData(String name, LinkedList<Integer> playerNums, LinkedList<Integer> strategies, 
			LinkedList<Integer> payoffs, LinkedList<Integer> gameNums) {
		WritableWorkbook workbook;
		try {
			workbook = Workbook.createWorkbook(new File(name + ".xls")); 
			WritableSheet sheet = workbook.createSheet("Data", 0);
			Label player = new Label(2,2,"Player Number");
			Label strategy = new Label(3,2,"Strategy");
			Label payoff = new Label(4,2,"Payoff");
			Label gameNum = new Label(5,2,"Game Number");
			sheet.addCell(player);
			sheet.addCell(strategy);
			sheet.addCell(payoff);
			sheet.addCell(gameNum);
			for (int i=0; i<playerNums.size(); i++) {
				Label thisPlayer = new Label(2,i+3,playerNums.get(i) + "");
				sheet.addCell(thisPlayer);
				Label thisStrategy = new Label(3,i+3,strategies.get(i) + "");
				sheet.addCell(thisStrategy);
				Label thisPayoff = new Label(4,i+3,payoffs.get(i) + "");
				sheet.addCell(thisPayoff);
				Label thisGameNums = new Label(5,i+3,gameNums.get(i) + "");
				sheet.addCell(thisGameNums);
			}
			workbook.write();
			workbook.close();
			return true;
		} catch (IOException | WriteException e) {
			e.printStackTrace();
			return false;
		}
	}
}
