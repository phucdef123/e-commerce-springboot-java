package com.assignment.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.assignment.dao.ItemTypeDAO;
import com.assignment.dao.ProductDAO;
import com.assignment.dao.ProductItemTypeDAO;
import com.assignment.entity.Category;
import com.assignment.entity.ItemType;
import com.assignment.entity.Product;
import com.assignment.entity.ProductItemType;
import com.assignment.entity.ProductItemTypeId;
import com.assignment.service.FileStorageService;
import com.assignment.service.SessionService;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/admin/product")
public class ProductController {
	@Autowired
	ProductDAO proDao;
	@Autowired
	ItemTypeDAO itDao;
	@Autowired
	ProductItemTypeDAO pitDao;
	@Autowired
    SessionService session;
	@Autowired
    private FileStorageService fileStorageService;
	
	@RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
		Product itemPro = proDao.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
		List<Integer> typeIds = itemPro.getProductItemType().stream()
	            .map(pit -> pit.getItemType().getTypeId())
	            .collect(Collectors.toList());

	    itemPro.setItemTypeIds(typeIds); // Gán vào biến @Transient
		redirectAttributes.addFlashAttribute("itemPro", itemPro);
	    return "redirect:/admin/home";
    }

	@RequestMapping("/create")
	@Transactional 
	public String create(Product item, 
			@RequestParam(required = false) List<Integer> itemTypeIds,
             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
	    try {
	        if (!imageFile.isEmpty()) {
	            String imagePath = fileStorageService.storeFile(imageFile);
	            item.setImage(imagePath);
	        }

	        if (item.getId() == null || item.getId().trim().isEmpty()) {
	            item.setId(null);
	        }

	        item.setRating(0.0);
	        proDao.save(item);
	        updateProductTypes(item, itemTypeIds);
	        return "redirect:/admin/home";
	    } catch (IllegalStateException e) {
	        System.out.println("Lỗi: " + e.getMessage());
	        return "redirect:/admin/home"; // Điều hướng trang lỗi để tránh rollback
	    } catch (Exception e) {
	        System.out.println("Lỗi không xác định: " + e.getMessage());
	        return "redirect:/admin/home"; // Điều hướng trang lỗi
	    }
	}


	@RequestMapping("/update")
	@Transactional
	public String update(Product item, 
	                     @RequestParam(required = false) List<Integer> itemTypeIds,
	                     @RequestParam("imageFile") MultipartFile imageFile) {
		if (!imageFile.isEmpty()) {
	        // Lưu file ảnh và cập nhật đường dẫn
	        String imagePath = fileStorageService.storeFile(imageFile);
	        item.setImage(imagePath); // Cập nhật đường dẫn vào đối tượng Product
	    }
	    Product itemPro = proDao.findById(item.getId()).orElseThrow(() -> new RuntimeException("Product not found"));
	    item.setRating(itemPro.getRating());
	    item.setRegisterDate(itemPro.getRegisterDate());
	    proDao.save(item);
	    if (itemTypeIds == null) {
	        itemTypeIds = new ArrayList<>(); 
	    }
	    updateProductTypes(item, itemTypeIds);
	    return "redirect:/admin/product/edit/" + item.getId();
	}


    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!proDao.existsById(id)) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy ID sản phẩm");
            return "redirect:/admin/home";
        }
        proDao.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công");
        return "redirect:/admin/home";
    }

   

    @RequestMapping("/sort")
    public String sort(Model model, @RequestParam("field") Optional<String> field) {
    	String sortField = field.orElse("id");
        
        String lastSortField = (String) session.get("proSortField");
        String lastSortDirection = (String) session.get("proSortDirection");
        String sortDirection;

        if(sortField.equals(lastSortField) && "desc".equalsIgnoreCase(lastSortDirection)) {
            sortDirection = "asc";
        } else {
            sortDirection = "desc";
        }
        
        session.set("proSortField", sortField);
        session.set("proSortDirection", sortDirection);
        
        return "redirect:/admin/home";
    }

    @RequestMapping("/page")
    public String paginate(RedirectAttributes redirectAttributes, @RequestParam("p") Optional<Integer> p) {
    	int pageNum = p.orElse(0);
        redirectAttributes.addAttribute("p", pageNum);
     
        return "redirect:/admin/home";
    }
    
    @Transactional
    public void updateProductTypes(Product product, List<Integer> selectedTypeIds) {
        // Nếu product.getProductItemType() null thì set List rỗng
        if (product.getProductItemType() == null) {
            product.setProductItemType(new ArrayList<>());
        }

        // Xóa các quan hệ cũ không có trong selectedTypeIds
        Iterator<ProductItemType> iterator = product.getProductItemType().iterator();
        while (iterator.hasNext()) {
            ProductItemType pit = iterator.next();
            if (!selectedTypeIds.contains(pit.getItemType().getTypeId())) {
                iterator.remove();  
            }
        }

        // Thêm quan hệ mới nếu chưa có
        for (Integer typeId : selectedTypeIds) {
            boolean exists = product.getProductItemType().stream()
                    .anyMatch(pit -> pit.getItemType().getTypeId().equals(typeId));
            if (!exists) {
                ItemType itemType = itDao.findById(typeId)
                        .orElseThrow(() -> new RuntimeException("ItemType not found"));
                ProductItemTypeId pitId = new ProductItemTypeId(product.getId(), typeId);
                ProductItemType pit = new ProductItemType();
                pit.setId(pitId);
                pit.setProduct(product);
                pit.setItemType(itemType);
                product.getProductItemType().add(pit);
                
            }
        }
        proDao.save(product);
    }





}
