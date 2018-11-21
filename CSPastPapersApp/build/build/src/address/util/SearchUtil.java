package address.util;

import address.model.Paper;
import address.model.Topic;
import javafx.collections.ObservableList;

public class SearchUtil {

	/**
	 * return topic based on parameter
	 * @param topics ObservableList of topics
	 * @param param search parameter (num / name / full)
	 * @param value value for search parameter
	 * @return topic in topics
	 */
	public static Topic searchTopic(ObservableList<Topic> topics, String param, String value) {
		
		switch (param) {
		
			case "num":
				int num = Integer.parseInt(value);
				for (Topic t : topics) {
					if (t.getNum() == num) {
						return t;
					}
				}
				break;
				
			case "name":
				for (Topic t : topics) {
					if (t.getName().equals(value)) {
						return t;
					}
				}
				break;
				
			case "full":
				for (Topic t : topics) {
					if (t.getFullTopic().equals(value)) {
						return t;
					}
				}
				break;

		}
		
		return null;
		
	}
	
	/**
	 * return paper based on parameter
	 * @param papers ObservableList of papers
	 * @param value value for search parameter
	 * @return paper in papers
	 */
	public static Paper searchPaper(ObservableList<Paper> papers, String value) {
		
		for (Paper p : papers) {
			if (p.getPaperID().equals(value)) {
				return p;
			}
		}
		
		return null;
		
	}
	
}
