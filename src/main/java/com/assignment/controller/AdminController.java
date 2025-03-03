package com.assignment.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.assignment.dao.CategoryDAO;
import com.assignment.dao.ItemTypeDAO;
import com.assignment.dao.OrderDAO;
import com.assignment.dao.OrderDetailDAO;
import com.assignment.dao.ProductDAO;
import com.assignment.dao.UserDAO;
import com.assignment.entity.Category;
import com.assignment.entity.ItemType;
import com.assignment.entity.Order;
import com.assignment.entity.OrderDetail;
import com.assignment.entity.Product;
import com.assignment.entity.User;
import com.assignment.service.SessionService;

@Controller
public class AdminController {
	@Autowired
	SessionService session;
	@Autowired
    private CategoryDAO cateDAO;

    @Autowired
    private ProductDAO proDAO;
    
    @Autowired
    private ItemTypeDAO itDAO;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private OrderDAO orderDAO;
    
    @Autowired
    private OrderDetailDAO odetailDAO;
    
    @ModelAttribute
    public void addAttributes(Model model) {
    	if (!model.containsAttribute("message")) {
            model.addAttribute("message", "Chào mừng đến với trang admin");
        }
    	if (session.get("user") != null) {
			model.addAttribute("userCr", session.get("user"));
		}
    	if (!model.containsAttribute("openEditModal")) {
            model.addAttribute("openEditModal", true);
        }
        if (!model.containsAttribute("itemCate")) {
            model.addAttribute("itemCate", new Category());
        }
        if (!model.containsAttribute("itemPro")) {
        	Product itemPro = new Product();
        	itemPro.setAvailable(true);
            model.addAttribute("itemPro", itemPro);
        }
        if (!model.containsAttribute("itemUser")) {
        	User user = new User();
        	user.setActivated(true);
            model.addAttribute("itemUser", user);
        }
        if (!model.containsAttribute("itemOrder")) {
            model.addAttribute("itemOrder", new Order());
            model.addAttribute("grandTotal", 0.0);
            model.addAttribute("totalAmount", 0.0);
        }
        
        model.addAttribute("cateList", cateDAO.findAll());
        model.addAttribute("itemTypeList", itDAO.findAll());
       
    }
    @RequestMapping("/admin/home")
    public String admin(
    		@RequestParam(value = "p", required = false, defaultValue = "0") int p,
            Model model) {
    	String cateSortField = (String) session.get("cateSortField");
    	if (cateSortField == null) {
    		cateSortField = "categoryId"; // giá trị mặc định
    	} 
    	String proSortField = (String) session.get("proSortField");
    	if (proSortField == null) {
    		proSortField = "id"; // giá trị mặc định
    	}
    	String userSortField = (String) session.get("userSortField");
    	if (userSortField == null) {
    		userSortField = "username"; // giá trị mặc định
    	}
    	String orderSortField = (String) session.get("orderSortField");
    	if (orderSortField == null) {
    		orderSortField = "id"; // giá trị mặc định
    	}
    	Pageable pageableCate = PageRequest.of(p, 5, Sort.by(resolveSortDirection("cateSortDirection", "asc"), cateSortField));    	
        Pageable pageablePro = PageRequest.of(p, 10, Sort.by(resolveSortDirection("proSortDirection", "asc"), proSortField));
        Pageable pageableUser = PageRequest.of(p, 10, Sort.by(resolveSortDirection("userSortDirection", "asc"), userSortField));
        Pageable pageableOrder;
        Page<Order> pageOrder;

        if ("totalPrice".equalsIgnoreCase(orderSortField)) {
            pageableOrder = PageRequest.of(p, 10); 
            String orderSortDirection = (String) session.get("orderSortDirection");
            pageOrder = (orderSortDirection.equalsIgnoreCase("asc")) ? orderDAO.sortByTotalPriceAsc(pageableOrder) : orderDAO.sortByTotalPriceDesc(pageableOrder);            
        } else {
            pageableOrder = PageRequest.of(p, 10, Sort.by(resolveSortDirection("orderSortDirection", "asc"), orderSortField));
            pageOrder = orderDAO.findAll(pageableOrder);
        }
        
        List<Map<String, Object>> vipCustomers = odetailDAO.getTopVipCustomers();
        List<Map<String, Object>> revenueData = odetailDAO.getRevenueByMonth();
        
        Map<String, Double> totalPriceMap = new HashMap<>();// Tạo Map chứa tổng tiền theo Order ID
        for (Order order : pageOrder.getContent()) {
            totalPriceMap.put(order.getId(), orderDAO.getTotalAmount(order.getId()));
        }   
        
        List<Object[]> rawData = odetailDAO.getCategorySales(2024);
        List<Map<String, Object>> categorySales = rawData.stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("category", obj[0]);       // category_name
            map.put("totalQuantity", obj[1]);  // SUM(od.quantity)
            return map;
        }).collect(Collectors.toList());

        model.addAttribute("vipCustomers", vipCustomers);
        model.addAttribute("categorySales", categorySales);       
        model.addAttribute("revenueData", revenueData);
        model.addAttribute("pageCate", cateDAO.findAll(pageableCate));
        model.addAttribute("pagePro", proDAO.findAll(pageablePro));
        model.addAttribute("pageUser", userDAO.findAll(pageableUser));
        model.addAttribute("pageOrder", pageOrder);
        model.addAttribute("totalPriceMap", totalPriceMap);
        model.addAttribute("totalRevenue", odetailDAO.getTotalRevenue());
        model.addAttribute("totalQuantitySold", odetailDAO.getTotalQuantitySold(2024));

        return "admin";
    }

    private Sort.Direction resolveSortDirection(String sessionKey, String defaultDirection) {
        Object value = session.get(sessionKey);
        String sortDirStr = (value instanceof String) ? (String) value : defaultDirection;
        return sortDirStr.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

}
