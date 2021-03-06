package admin.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nkl.admin.manager.AdminManager;
import com.nkl.common.util.DateUtil;
import com.nkl.common.util.Md5;
import com.nkl.common.util.PaperUtil;
import com.nkl.page.domain.Logistics;
import com.nkl.page.domain.User;

@Controller
public class AdminAction{

	@Autowired
	AdminManager adminManager;
	public AdminManager getAdminManager() {
		return adminManager;
	}
	public void setAdminManager(AdminManager adminManager) {
		this.adminManager = adminManager;
	}

	String tip;
	
	/**
	 * @Title: saveAdmin
	 * @Description: Store the modified information
	 * @return String
	 */
	@RequestMapping(value="admin/Admin_saveAdmin.action")
	public String saveAdmin(User paramsUser,
			ModelMap model,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
		try {
			//Verify that the user session is invalid.
			if (!validateAdmin(httpSession)) {
				return "loginTip";
			}
			 //Store the modified information
			adminManager.updateUser(paramsUser);
			//Update session
			User admin = new User();
			admin.setUser_id(paramsUser.getUser_id());
			admin = adminManager.getUser(admin);
			httpSession.setAttribute("admin", admin);

			setSuccessTip("Edition sucessful", "modifyInfo.jsp", model);
		} catch (Exception e) {
			e.printStackTrace();
			setErrorTip("Edition exception", "modifyInfo.jsp", model);
		}
		return "infoTip";
	}
	
	/**
	 * @Title: saveAdminPass
	 * @Description: Store modified password
	 * @return String
	 */
	@RequestMapping(value="admin/Admin_saveAdminPass.action")
	public String saveAdminPass(User paramsUser,
			ModelMap model,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
		try {
			//Verify that the user session is invalid.
			if (!validateAdmin(httpSession)) {
				return "loginTip";
			}
			//Verify the stored password.
			User admin = (User)httpSession.getAttribute("admin");
			String user_passOld1 = admin.getUser_pass();
			String user_passOld2 = Md5.makeMd5(paramsUser.getUser_passOld());
			if (!user_passOld1.equals(user_passOld2)) {
				setErrorTip("The password in vaild", "modifyPwd.jsp", model);
				return "infoTip";
			}
			 //Stored the modified password.
			adminManager.updateUser(paramsUser);
			//Update session
			if (admin!=null) {
				admin.setUser_pass(paramsUser.getUser_pass());
				httpSession.setAttribute("admin", admin);
			}

			setSuccessTip("Edition sucessful", "modifyPwd.jsp", model);
		} catch (Exception e) {
			setErrorTip("Edition exception", "modifyPwd.jsp", model);
		}
		return "infoTip";
	}
	
	/**
	 * @Title: listUsers
	 * @Description: Search user
	 * @return String
	 */
	@RequestMapping(value="admin/Admin_listUsers.action")
	public String listUsers(User paramsUser,PaperUtil paperUtil,
			ModelMap model,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
		try {
			if (paramsUser==null) {
				paramsUser = new User();
			}
			if (paperUtil==null) {
				paperUtil = new PaperUtil();
			}
			//Set paging information
			paperUtil.setPagination(paramsUser);
			//The total pages
			int[] sum={0};
			//Search the list of users
			paramsUser.setUser_type(1);
			List<User> users = adminManager.listUsers(paramsUser,sum); 
			model.addAttribute("users", users);
			model.addAttribute("paramsUser", paramsUser);
			paperUtil.setTotalCount(sum[0]);

		} catch (Exception e) {
			setErrorTip("Searching exception", "main.jsp", model);
			return "infoTip";
		}
		
		return "userShow";
	}
	
	/**
	 * @Title: addUserShow
	 * @Description: Show the page of adding user.
	 * @return String
	 */
	@RequestMapping(value="admin/Admin_addUserShow.action")
	public String addUserShow(ModelMap model){
		return "userEdit";
	}
	
	/**
	 * @Title: addUser
	 * @Description: Add user
	 * @return String
	 */
	@RequestMapping(value="admin/Admin_addUser.action")
	public String addUser(User paramsUser,
			ModelMap model,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
		try {
			//Check user vaild
			User user = new User();
			user.setUser_name(paramsUser.getUser_name());
			user = adminManager.getUser(user);
			if (user!=null) {
				model.addAttribute("tip","Fail, the user name is vaild！");
				model.addAttribute("user", paramsUser);
				
				return "userEdit";
			}
			 //Add user
			paramsUser.setUser_type(1);
			paramsUser.setReg_date(DateUtil.getCurDateTime());
			adminManager.addUser(paramsUser);
			
			setSuccessTip("Add sucessful", "Admin_listUsers.action", model);
		} catch (Exception e) {
			setErrorTip("Add exception", "Admin_listUsers.action", model);
		}
		
		return "infoTip";
	}
	
	 
	/**
	 * @Title: editUser
	 * @Description: Edit user
	 * @return String
	 */
	@RequestMapping(value="admin/Admin_editUser.action")
	public String editUser(User paramsUser,
			ModelMap model,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
		try {
			 //Get user
			User user = adminManager.getUser(paramsUser);
			model.addAttribute("user", user);
			
		} catch (Exception e) {
			setErrorTip("Searching exception", "Admin_listUsers.action", model);
			return "infoTip";
		}
		
		return "userEdit";
	}
	
	/**
	 * @Title: saveUser
	 * @Description: Store modified user
	 * @return String
	 */
	@RequestMapping(value="admin/Admin_saveUser.action")
	public String saveUser(User paramsUser,
			ModelMap model,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
		try {
			 //Store modified user
			adminManager.updateUser(paramsUser);
			
			setSuccessTip("Edition sucessful", "Admin_listUsers.action", model);
		} catch (Exception e) {
			setErrorTip("Edition exception", "Admin_listUsers.action", model);
			return "infoTip";
		}
		
		return "infoTip";
	}
	
	/**
	 * @Title: delUsers
	 * @Description: Delete user
	 * @return String
	 */
	@RequestMapping(value="admin/Admin_delUsers.action")
	public String delUsers(User paramsUser,
			ModelMap model,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
		try {
			 //delete user
			adminManager.delUsers(paramsUser);
			
			setSuccessTip("Deletion sucessful", "Admin_listUsers.action", model);
		} catch (Exception e) {
			setErrorTip("Deletion exception", "Admin_listUsers.action", model);
		}
		
		return "infoTip";
	}
	
	/**
	 * @Title: listAreas
	 * @Description: 
	 * @return String
	 */
	@RequestMapping(value="admin/Admin_listGoodss.action")
	public String listGoodss(Goods paramsGoods,PaperUtil paperUtil,
			ModelMap model,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
		try {
			if (paramsGoods==null) {
				paramsGoods = new Goods();
			}
			//Veridify the paging information
			paperUtil.setPagination(paramsGoods);
			int[] sum={0};
			List<Goods> goodss = adminManager.listGoodss(paramsGoods,sum); 
			
			model.addAttribute("goodss", goodss);
			paperUtil.setTotalCount(sum[0]);
			
		} catch (Exception e) {
			setErrorTip("Search","main.jsp",model);
			return "infoTip";
		}
		
		return "goodsShow";
	}
	

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}


}