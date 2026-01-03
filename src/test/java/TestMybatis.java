import com.yjx.note.util.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class TestMybatis {
    @Test
    public void test(){
        try(SqlSession sqlSession = MybatisUtils.getSqlSession())
        {
            System.out.println(sqlSession);
        }
    }
}