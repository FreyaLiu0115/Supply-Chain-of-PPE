package admin.manager;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nkl.common.util.Md5;
import com.nkl.common.util.StringUtil;
import com.nkl.page.dao.IUserDao;
import com.nkl.page.domain.User;

@Service
public class AdminManager {
	
	@Autowired
	IUserDao userDao;
	
	
	/**
	 * @Title: listUsers
	 * @Description: Search user
	 * @param user
	 * @return List<User>
	 */
	public List<User>  listUsers(User user,int[] sum){
		
		if (sum!=null) {
			sum[0] = userDao.listUsersCount(user);
		}
		List<User> users = userDao.listUsers(user);
		
		
		return users;
	}
	
	/**
	 * @Title: getUser
	 * @Description: searching user
	 * @param user
	 * @return User
	 */
	public User  getUser(User user){
		
		User _user = userDao.getUser(user);
		
		return _user;
	}
	 
	/**
	 * @Title: addUser
	 * @Description: Add user
	 * @param user
	 * @return void
	 */
	public void  addUser(User user){
		
		if (!StringUtil.isEmptyString(user.getUser_pass())) {
			user.setUser_pass(Md5.makeMd5(user.getUser_pass()));
		}
		userDao.addUser(user);
		
	}
	
	/**
	 * @Title: updateUser
	 * @Description: Update user information
	 * @param user
	 * @return void
	 */
	public void  updateUser(User user){
		
		if (!StringUtil.isEmptyString(user.getUser_pass())) {
			user.setUser_pass(Md5.makeMd5(user.getUser_pass()));
		}
		userDao.updateUser(user);
		
	}
	
	/**
	 * @Title: delUsers
	 * @Description: Delete user information
	 * @param user
	 * @return void
	 */
	public void  delUsers(User user){
		
		userDao.delUsers(user.getIds().split(","));
		
	}
	
	
	
}