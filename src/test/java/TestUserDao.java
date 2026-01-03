import com.yjx.note.dao.UserDao;
import com.yjx.note.po.User;
import com.yjx.note.util.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class TestUserDao {

    @Test
    public void testQueryUserByName() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        User user = userDao.queryUserByName("周君烨");
        System.out.println(user);
        sqlSession.close(); // 建议显式关闭（虽然 MyBatisUtils 用了 openSession(true) 自动提交，但 close 更安全）
    }
}