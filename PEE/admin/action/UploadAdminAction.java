package admin.action;

import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.nkl.common.util.DateUtil;
import com.nkl.common.util.FindProjectPath;
import com.nkl.common.util.UploadFile;

@Controller
public class UploadAdminAction {
	public static String path = "prop/database.properties";  
	//The relative path of the property file that saves the database connection information
	public static Properties props = new Properties();
	static{
		props = new Properties();
		try {
			props.load(UploadAdminAction.class.getClassLoader().getResourceAsStream(path));
		} catch (Exception e) {
			props = new Properties();
		}
	}
	
	/**
	 * @Title: UploadImg
	 * @Description: Upload file
	 * @return String
	 */
	@RequestMapping(value="admin/UploadImg.action",method=RequestMethod.POST)
	public String UploadImg(@RequestParam("upload") MultipartFile file,String num,
			ModelMap model,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
		String returnPage = "uploadImg";
		try {
			//Rename the image
			String old_name=file.getOriginalFilename();
			String file_name=DateUtil.dateToDateString(new Date(),"yyyyMMddHHmmssSSS")+old_name.substring(old_name.indexOf("."));
			//Set save file location
			String savePath = props.getProperty("savePath");
			if ("1".equals(num)) {
				savePath = props.getProperty("savePath1");
				returnPage = returnPage+"1";
			}else if ("2".equals(num)) {
				savePath = props.getProperty("savePath2");
				returnPage = returnPage+"2";
			}else if ("3".equals(num)) {
				savePath = props.getProperty("savePath3");
				returnPage = returnPage+"3";
			}
			String saveFile=FindProjectPath.getRootPath(savePath+"\\"+file_name);
			//File type restrictions
			String allowedTypes = props.getProperty("allowedTypes");
			if ("1".equals(num)) {
				allowedTypes = props.getProperty("allowedTypes1");
			}else if ("2".equals(num)) {
				allowedTypes = props.getProperty("allowedTypes2");
			}else if ("3".equals(num)) {
				allowedTypes = props.getProperty("allowedTypes3");
			}
			//Upload file
			String errorString=UploadFile.upload(file, saveFile, file.getContentType(), file.getSize(), allowedTypes,Long.parseLong(props.getProperty("maximunSize")));
			//Judging the upload result
			if(!"".equals(errorString))
			{
				System.out.println(errorString);
				model.addAttribute("tip", "no");
				model.addAttribute("errorString", errorString);
				return returnPage;
			}
			model.addAttribute("tip", "ok");
			model.addAttribute("filename",file_name);
			model.addAttribute("filenameGBK",old_name);
			model.addAttribute("filelength",Math.round(file.getSize()/1024.0));
			return returnPage;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			model.addAttribute("tip", "no");
			model.addAttribute("errorString", "Background server exception");
			return returnPage;
		}
	}
}