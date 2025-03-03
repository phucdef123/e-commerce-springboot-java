package com.assignment.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.assignment.dao.UserDAO;
import com.assignment.entity.User;
import com.assignment.service.SessionService;

@Controller
@RequestMapping("/admin/user")
public class UserController {
	@Autowired
	UserDAO userDao;
	
	@Autowired
    SessionService session;

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
    	User itemUser = userDao.findById(id).orElseThrow(() -> new RuntimeException("User not found"));    	        
	    redirectAttributes.addFlashAttribute("itemUser", itemUser);
	    return "redirect:/admin/home";
    }

    @RequestMapping("/create")
    public String create(User item) {
    	userDao.save(item);
        return "redirect:/admin/home";
    }

    @RequestMapping("/update")
    public String update(User item) {
    	User itemUser = userDao.findById(item.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));  
        item.setPassword(itemUser.getPassword());
        item.setAdmin(itemUser.isAdmin());
    	userDao.save(item);
        return "redirect:/admin/user/edit/" + item.getUsername();
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
    	if (!userDao.existsById(id)) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy Username");
            return "redirect:/admin/home";
        }
        userDao.deleteById(id);
        return "redirect:/admin/home";
    }

    @RequestMapping("/sort")
    public String sort(@RequestParam("field") Optional<String> field) {
        String sortField = field.orElse("username");
        
        String lastSortField = (String) session.get("userSortField");
        String lastSortDirection = (String) session.get("userSortDirection");
        String sortDirection;

        if(sortField.equals(lastSortField) && "desc".equalsIgnoreCase(lastSortDirection)) {
            sortDirection = "asc";
        } else {
            sortDirection = "desc";
        }
        
        session.set("userSortField", sortField);
        session.set("userSortDirection", sortDirection);
        
        return "redirect:/admin/home";
    }

    @RequestMapping("/page")
    public String paginate(RedirectAttributes redirectAttributes, @RequestParam("p") Optional<Integer> p) {
        int pageNum = p.orElse(0);
        redirectAttributes.addAttribute("p", pageNum);
     
        return "redirect:/admin/home";
    }
    
    @RequestMapping("/update-profile")
	public String updateProfile(User itemUser) {
		User user = userDao.findById(itemUser.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));  
    	if (itemUser.getPassword() == null || itemUser.getPassword().trim().isBlank()) {
    		itemUser.setPassword(user.getPassword());
		}
    	itemUser.setAdmin(user.isAdmin());
    	itemUser.setActivated(true);
    	itemUser.setHomeAddress(user.getHomeAddress());
    	itemUser.setWorkAddress(user.getWorkAddress());
    	userDao.save(itemUser);
    	return "redirect:/admin/home";
	}
}
