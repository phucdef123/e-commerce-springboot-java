package com.assignment.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.assignment.dao.OrderDAO;
import com.assignment.entity.Category;
import com.assignment.entity.Order;
import com.assignment.entity.OrderDetail;
import com.assignment.service.SessionService;

@Controller
@RequestMapping("/admin/order")
public class OrderController {
	@Autowired
	OrderDAO orderDao;
	
	@Autowired
	SessionService session;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	@RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
    	Order itemOrder = orderDao.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));   
    	List<OrderDetail> odetailList = orderDao.findListOrderDetailById(id);
    	double totalAmount = odetailList.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();   	
    	double grandTotal = totalAmount + itemOrder.getShippingFee();	    
    		    
    	redirectAttributes.addFlashAttribute("odetailList", odetailList);
	    redirectAttributes.addFlashAttribute("itemOrder", itemOrder);
	    redirectAttributes.addFlashAttribute("openEditModal", true);
	    redirectAttributes.addFlashAttribute("totalAmount", totalAmount);
	    redirectAttributes.addFlashAttribute("grandTotal", grandTotal);
	    
	    return "redirect:/admin/home";
    }
	
	@RequestMapping("/update")
    public String update(Order item, RedirectAttributes redirectAttributes) {
        orderDao.save(item);
        redirectAttributes.addFlashAttribute("openEditModal", false);
        return "redirect:/admin/home";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {
        orderDao.deleteById(id);
        return "redirect:/admin/home";
    }

    @RequestMapping("/sort")
    public String sort(@RequestParam("field") Optional<String> field) {
        String sortField = field.orElse("id");
        
        String lastSortField = (String) session.get("orderSortField");
        String lastSortDirection = (String) session.get("orderSortDirection");
        String sortDirection;

        if(sortField.equals(lastSortField) && "desc".equalsIgnoreCase(lastSortDirection)) {
            sortDirection = "asc";
        } else {
            sortDirection = "desc";
        }
        
        session.set("orderSortField", sortField);
        session.set("orderSortDirection", sortDirection);
        
        return "redirect:/admin/home";
    }

    @RequestMapping("/page")
    public String paginate(RedirectAttributes redirectAttributes, @RequestParam("p") Optional<Integer> p) {
        int pageNum = p.orElse(0);
        redirectAttributes.addAttribute("p", pageNum);
     
        return "redirect:/admin/home";
    }
}
