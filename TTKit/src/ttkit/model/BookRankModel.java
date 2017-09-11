package ttkit.model;

import java.util.List;

/**
*  类名 BookRankModel
*  @author xfy
*  eg: 排行榜接口示例
*/
public class BookRankModel extends BaseModelJsonDemo {
	public class BookRankModelItem{
		public String id;
		public String name;
		public String author;
		public String cover;
		public String press;
		public String publish_time;
		public String tags;
	}
	public class BookRankModelValue{
		public int total_page;
		public List<BookRankModelItem>  books;		
	}
	public BookRankModelValue target;

}
