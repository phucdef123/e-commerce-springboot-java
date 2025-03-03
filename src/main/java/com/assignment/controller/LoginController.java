package com.assignment.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.assignment.dao.OrderDAO;
import com.assignment.dao.UserDAO;
import com.assignment.entity.Order;
import com.assignment.entity.OrderDetail;
import com.assignment.entity.User;
import com.assignment.service.MailService;
import com.assignment.service.SessionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class LoginController {
	@Autowired
	UserDAO userDao;
	
	@Autowired
	OrderDAO orderDao;

	@Autowired
	SessionService session;
	
	@Autowired
    private MailService mailService;

	@PostMapping("/login")
	public String loginProcess(User itemUser, RedirectAttributes redirectAttributes) {
		User user = userDao.findById(itemUser.getUsername()).orElse(null);
		if (user == null) {
			redirectAttributes.addFlashAttribute("message", "Invalid email!");
		} else if (!user.getPassword().equals(itemUser.getPassword())) {
			redirectAttributes.addFlashAttribute("message", "Invalid password!");
		} else {
			session.set("user", user);
			redirectAttributes.addFlashAttribute("message", "Login successfully!");
			if (user.isAdmin()) {
				return "redirect:/admin/home";
			}
			String securityUri = (String) session.get("securityUri");
			if (securityUri != null) {
				return "redirect:" + securityUri;
			}

		}

		return "redirect:/";
	}
	
	@RequestMapping("/logout")//, method=RequestMethod.GET
	public String logout() {
		session.remove("user");
		return "redirect:/";
	}
	
	@RequestMapping("/register")
	public String register(User user, RedirectAttributes redirectAttributes) {
		user.setActivated(true);
		user.setAdmin(false);
		userDao.save(user);
		redirectAttributes.addFlashAttribute("message", "Đăng kí thành công");
		return "redirect:/";
	}
	
	@RequestMapping("/account/profile")
	public String account(Model model) {
		User user = (User) session.get("user");
		if (user == null) {
			return "redirect:/";
		}
		List<Order> orderList = orderDao.findByUserUsername(user.getUsername());
		Map<String, Double> totalPriceMap = new HashMap<>();
		for (Order order : orderList) {
            totalPriceMap.put(order.getId(), orderDao.getTotalAmount(order.getId()));
        }
		
		model.addAttribute("orderList", orderList);
		model.addAttribute("user", user);
		model.addAttribute("totalPriceMap", totalPriceMap);		
	    
		return "account-detail";
	}
	
	@PostMapping("/account/update-profile")
	public String accountUpdate(Model model, User userItem) {
		User user = userDao.findById(userItem.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
		if (userItem.getPassword() == null || userItem.getPassword().trim().isBlank()) {
			userItem.setPassword(user.getPassword());
		}
		userItem.setActivated(true);
		userItem.setAdmin(user.isAdmin());
		
		userDao.save(userItem);
		session.set("user", userItem);
		//model.addAttribute(userItem);
		return "redirect:/account/profile";
	}
	
	@RequestMapping("/account/order/edit/{id}")
	public String editOrder(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Order order = orderDao.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order == null) {
            return "redirect:/account-detail";
        }
        List<OrderDetail> odetailList = orderDao.findListOrderDetailById(id);
        double totalAmount = odetailList.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();   	
    	double grandTotal = totalAmount + order.getShippingFee();
    	
    	redirectAttributes.addFlashAttribute("totalAmount", totalAmount);
	    redirectAttributes.addFlashAttribute("grandTotal", grandTotal);
        redirectAttributes.addFlashAttribute("order", order);
        redirectAttributes.addFlashAttribute("odetailList", odetailList);
        
        return "redirect:/account/profile"; 
    }
	
	@RequestMapping("/account/order/update/{id}")
	public String updateOrder(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Order order = orderDao.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order == null) {
            return "redirect:/account-detail";
        }
      
        order.setStatus("Canceled");
        orderDao.save(order);
       
        redirectAttributes.addFlashAttribute("order", order);
        
        return "redirect:/account/profile"; 
    }
	
	@GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password"; 
    }
	
	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, Model model) {
	    User user = userDao.findByEmail(email);

	    if (user == null) {
	        model.addAttribute("error", "Email không tồn tại trong hệ thống!");
	        return "forgot-password";
	    }

	    String token = UUID.randomUUID().toString();
	    LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(30); // Hết hạn sau 30 phút

	    session.set("resetToken", token);System.out.println("Token nhận được từ URL: " + token);
	    session.set("resetTokenExpiry", expiryTime);System.out.println("Thời gian hết hạn: " + expiryTime);
	    session.set("resetUser", user);

	    String resetLink = "http://localhost:8080/reset-password?token=" + token;
	    mailService.send(email, "Đặt lại mật khẩu", "Nhấp vào đây để đặt lại mật khẩu: <a href='" + resetLink + "'>Đặt lại mật khẩu</a>");

	    model.addAttribute("message", "Hãy kiểm tra email để đặt lại mật khẩu!");
	    return "forgot-password";
	}


	
	@GetMapping("/reset-password")
	public String showResetPasswordPage(@RequestParam String token, Model model) {
	    String sessionToken = (String) session.get("resetToken");
	    LocalDateTime expiryTime = (LocalDateTime) session.get("resetTokenExpiry");

	    if (sessionToken == null || expiryTime == null || expiryTime.isBefore(LocalDateTime.now()) || !sessionToken.equals(token)) {
	        model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn!");
	        return "forgot-password";
	    }

	    return "reset-password"; 
	}


	
	@PostMapping("/reset-password")
	public String processResetPassword(@RequestParam(required = false) String token, 
	                                   @RequestParam String newPassword, 
	                                   Model model) {
	    String sessionToken = (String) session.get("resetToken");
	    User user = (User) session.get("resetUser");

	    if (token == null || token.isEmpty() || sessionToken == null || user == null || !sessionToken.equals(token)) {
	        model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn!");
	        return "forgot-password";
	    }

	    user.setPassword(newPassword); 
	    userDao.save(user);

	    session.remove("resetToken");
	    session.remove("resetTokenExpiry");
	    session.remove("resetUser");

	    model.addAttribute("message", "Mật khẩu đã được cập nhật thành công!");
	    return "redirect:/?loginRequired=true"; 
	}


}
