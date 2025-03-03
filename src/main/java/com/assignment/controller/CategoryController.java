package com.assignment.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.assignment.dao.CategoryDAO;
import com.assignment.dao.ProductDAO;
import com.assignment.entity.Category;
import com.assignment.service.SessionService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    CategoryDAO cateDao;
    
    @Autowired
    ProductDAO proDao;
    @Autowired
    SessionService session;

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
    	Category itemCate = cateDao.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));    	        
	    redirectAttributes.addFlashAttribute("itemCate", itemCate);
	    return "redirect:/admin/home";
    }

    @RequestMapping("/create")
    public String create(Category item) {
        cateDao.save(item);
        return "redirect:/admin/home";
    }

    @RequestMapping("/update")
    public String update(Category item) {
        cateDao.save(item);
        return "redirect:/admin/category/edit/" + item.getCategoryId();
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
    	if (proDao.countByCategoryId(id) > 0) {
    		redirectAttributes.addFlashAttribute("message", "Không thể xóa Category này!!");
    		return "redirect:/admin/home";
		}
        cateDao.deleteById(id);
        return "redirect:/admin/home";
    }

    @RequestMapping("/sort")
    public String sort(@RequestParam("field") Optional<String> field) {
        String sortField = field.orElse("categoryId");
        
        String lastSortField = (String) session.get("cateSortField");
        String lastSortDirection = (String) session.get("cateSortDirection");
        String sortDirection;

        if(sortField.equals(lastSortField) && "desc".equalsIgnoreCase(lastSortDirection)) {
            sortDirection = "asc";
        } else {
            sortDirection = "desc";
        }
        
        session.set("cateSortField", sortField);
        session.set("cateSortDirection", sortDirection);
        
        return "redirect:/admin/home";
    }

    @RequestMapping("/page")
    public String paginate(RedirectAttributes redirectAttributes, @RequestParam("p") Optional<Integer> p) {
        int pageNum = p.orElse(0);
        redirectAttributes.addAttribute("p", pageNum);
     
        return "redirect:/admin/home";
    }
}
