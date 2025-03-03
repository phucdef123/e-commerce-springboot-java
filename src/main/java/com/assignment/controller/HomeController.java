package com.assignment.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.assignment.dao.CartDAO;
import com.assignment.dao.CategoryDAO;
import com.assignment.dao.ItemTypeDAO;
import com.assignment.dao.OrderDAO;
import com.assignment.dao.OrderDetailDAO;
import com.assignment.dao.ProductDAO;
import com.assignment.dao.UserDAO;
import com.assignment.entity.Cart;
import com.assignment.entity.Category;
import com.assignment.entity.Order;
import com.assignment.entity.OrderDetail;
import com.assignment.entity.Product;
import com.assignment.entity.User;
import com.assignment.service.CookieService;
import com.assignment.service.SessionService;

import jakarta.servlet.http.Cookie;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class HomeController {
	@Autowired
	SessionService session;
	
	@Autowired
	CookieService cookie;
	
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
    private OrderDetailDAO orderDetailDAO;
    
    @Autowired
    private CartDAO cartDAO;
	
	@ModelAttribute
    public void addAttributes(Model model) {
    	if (!model.containsAttribute("message")) {
            model.addAttribute("message", "Chào mừng đến với Shopdee");
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
        if (!model.containsAttribute("itemCart")) {
            model.addAttribute("itemCart", new Cart());
        }
        if (!model.containsAttribute("itemOrder")) {
            model.addAttribute("itemOrder", new Order());
            model.addAttribute("grandTotal", 0.0);
            model.addAttribute("totalAmount", 0.0);
        }
        model.addAttribute("productList", proDAO.findAll());
        model.addAttribute("cateList", cateDAO.findAll().stream().limit(5).collect(Collectors.toList()));
		model.addAttribute("cateListShop", cateDAO.findAll());
        model.addAttribute("itemTypeList", itDAO.findAll());
        model.addAttribute("proListSortByRegisterDate", proDAO.findAllByOrderByRegisterDateDesc().stream().limit(10).collect(Collectors.toList()));
       
    }
	
	@RequestMapping("/")
	public String index(Model model) {				
		
		return "index";
	}
	
	@GetMapping("/shop/search")
	public String searchProducts(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model) {
		Pageable pageablePro = PageRequest.of(0, 9, Sort.by(Sort.Direction.ASC, "id"));
		
		Page<Product> products = keyword.isEmpty() ? proDAO.findAll(pageablePro) : proDAO.findByNameContainingIgnoreCase(keyword, pageablePro);
	    model.addAttribute("pagePro", products);
	    model.addAttribute("keyword", keyword);
	    return "shop"; 
	}
	
	@RequestMapping("/shop")
	public String shop(
			@RequestParam(value = "p", required = false, defaultValue = "0") int p,
			@RequestParam(value = "categoryId", required = false) Long categoryId, 
			Model model) {
		
		Pageable pageablePro = PageRequest.of(p, 9, Sort.by(Sort.Direction.ASC, "id"));
		Page<Product> pagePro;
	    if (categoryId != null) {
	        pagePro = proDAO.findByCategoryCategoryId(categoryId, pageablePro); 
	        model.addAttribute("selectedCategoryId", categoryId);
	    } else {
	        pagePro = proDAO.findAll(pageablePro);
	    }
	    
		model.addAttribute("pagePro", pagePro);
		return "shop";
	}
	
	@RequestMapping("/shop/page")
    public String paginate(RedirectAttributes redirectAttributes, @RequestParam("p") Optional<Integer> p) {
    	int pageNum = p.orElse(0);
        redirectAttributes.addAttribute("p", pageNum);
     
        return "redirect:/shop";
    }
	
	@RequestMapping("/shop-detail/{id}")
	public String detail(Model model, @PathVariable("id") String id) {
		Product itemPro = proDAO.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
		//System.out.println("DEBUG rating = " + itemPro.getRating());
		model.addAttribute("itemPro",itemPro);
		return "shop-detail";
	}
	
	@RequestMapping("/cart")
	public String cart(Model model) {
		User user = (User) session.get("user");
		List<Cart> cartList = new ArrayList<>();
		double totalPrice = 0.00;
		double shipFee = 25000.00;
		
		if (user != null) {
			mergeCartFromCookieToDB(user); // Chuyển dữ liệu từ Cookie vào DB
			cartList = cartDAO.findByUserUsername(user.getUsername());
			
		} else {
			Cookie cartCookie = cookie.get("cart");
			if (cartCookie != null) {
				String[] items = cartCookie.getValue().split("\\|");
				for (String item : items) {
					String[] parts = item.split(":");
					if (parts.length == 2) {
						String productId = parts[0];
						int quantity = Integer.parseInt(parts[1]);	
						Product product = proDAO.findById(productId).orElseThrow();
						Cart cart = new Cart();
						cart.setProduct(product);
						cart.setQuantity(quantity);
						cartList.add(cart);
					}
				}
			}

		}
		for (Cart cartItem : cartList) {
	        totalPrice += cartItem.getQuantity() * cartItem.getProduct().getPrice();
	    }
		model.addAttribute("cartList", cartList);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("shipFee", shipFee);
		return "cart";
	}
	
	@RequestMapping("/cart/add")
	public String cartAdd(Model model, 
	                      @RequestParam("id") String productId, 
	                      @RequestParam("quantity") int quantity, 
	                      RedirectAttributes redirectAttributes) {
	    Product itemPro = proDAO.findById(productId)
	        .orElseThrow(() -> new RuntimeException("Product not found"));
	    
	    User user = (User) session.get("user");

	    if (user != null) { 
	        mergeCartFromCookieToDB(user); // Chuyển dữ liệu từ Cookie vào DB

	        Cart cartItem = cartDAO.findByUserUsernameAndProductId(user.getUsername(), productId);
	        if (cartItem != null) {
	            cartItem.setQuantity(cartItem.getQuantity() + quantity);
	        } else {
	            cartItem = new Cart();
	            cartItem.setUser(user);
	            cartItem.setProduct(itemPro);
	            cartItem.setQuantity(quantity);
	        }
	        cartDAO.save(cartItem);
	        redirectAttributes.addFlashAttribute("message", "Đã cập nhật giỏ hàng với sản phẩm " + itemPro.getName());
	    } else {
	        // Nếu chưa đăng nhập -> Lưu vào Cookie
	        Cookie cartCookie = cookie.get("cart");
	        String cartValue = (cartCookie == null) ? "" : cartCookie.getValue();
	        Map<String, Integer> cartMap = new HashMap<>();

	        if (!cartValue.isEmpty()) {
	            String[] items = cartValue.split("\\|"); 
	            for (String item : items) {
	                String[] parts = item.split(":");
	                if (parts.length == 2) {
	                    String id = parts[0];
	                    int qty = Integer.parseInt(parts[1]);
	                    cartMap.put(id, qty);
	                }
	            }
	        }

	        // Nếu sản phẩm đã có trong giỏ, tăng số lượng, nếu chưa có thì thêm mới
	        cartMap.put(productId, cartMap.getOrDefault(productId, 0) + quantity);

	        String updatedCart = cartMap.entrySet().stream()
	            .map(entry -> entry.getKey() + ":" + entry.getValue())
	            .collect(Collectors.joining("|"));

	        cookie.add("cart", updatedCart, 30); // Cập nhật Cookie
	    }

	    redirectAttributes.addFlashAttribute("message", "Đã thêm " + itemPro.getName() + " vào giỏ hàng");
	    return "redirect:/shop-detail/" + productId;
	}
	
	public void mergeCartFromCookieToDB(User user) {
	    Cookie cartCookie = cookie.get("cart");
	    if (cartCookie == null || cartCookie.getValue().isEmpty()) {
	        return; // Không có dữ liệu giỏ hàng trong Cookie
	    }

	    String cartValue = cartCookie.getValue();
	    Map<String, Integer> cartMap = new HashMap<>();

	    String[] items = cartValue.split("\\|");
	    for (String item : items) {
	        String[] parts = item.split(":");
	        if (parts.length == 2) {
	            String id = parts[0];
	            int qty = Integer.parseInt(parts[1]);
	            cartMap.put(id, qty);
	        }
	    }

	    // Duyệt qua danh sách sản phẩm trong Cookie và thêm vào DB
	    for (Map.Entry<String, Integer> entry : cartMap.entrySet()) {
	        String productId = entry.getKey();
	        int quantity = entry.getValue();

	        Product product = proDAO.findById(productId).orElse(null);
	        if (product != null) {
	            Cart cartItem = cartDAO.findByUserUsernameAndProductId(user.getUsername(), productId);
	            if (cartItem != null) {
	                cartItem.setQuantity(cartItem.getQuantity() + quantity); // Cộng dồn số lượng
	            } else {
	                cartItem = new Cart();
	                cartItem.setUser(user);
	                cartItem.setProduct(product);
	                cartItem.setQuantity(quantity);
	            }
	            cartDAO.save(cartItem);
	        }
	    }

	    // Xóa Cookie sau khi chuyển xong
	    cookie.remove("cart");
	}

	@PostMapping("/cart/delete")
	public String deleteCartItem(@RequestParam("id") String productId, RedirectAttributes redirectAttributes) {
	    User user = (User) session.get("user");
	    
	    if (user != null) {
	        Cart cartItem = cartDAO.findByUserUsernameAndProductId(user.getUsername(), productId);
	        if (cartItem != null) {
	            cartDAO.delete(cartItem);
	            redirectAttributes.addFlashAttribute("message", "Đã xóa sản phẩm khỏi giỏ hàng");
	        }
	    } else {
	        // Nếu chưa đăng nhập, xóa khỏi Cookie
	        Cookie cartCookie = cookie.get("cart");
	        if (cartCookie != null) {
	            String cartValue = cartCookie.getValue();
	            Map<String, Integer> cartMap = new HashMap<>();
	            
	            // Phân tích giỏ hàng từ cookie
	            for (String item : cartValue.split("\\|")) {
	                String[] parts = item.split(":");
	                if (parts.length == 2) {
	                    String id = parts[0];
	                    int qty = Integer.parseInt(parts[1]);
	                    cartMap.put(id, qty);
	                }
	            }

	            // Xóa sản phẩm khỏi giỏ hàng
	            cartMap.remove(productId);

	            // Cập nhật cookie
	            String updatedCart = cartMap.entrySet().stream()
	                .map(entry -> entry.getKey() + ":" + entry.getValue())
	                .collect(Collectors.joining("|"));

	            cookie.add("cart", updatedCart, 30);
	        }
	    }

	    return "redirect:/cart"; // Quay lại trang giỏ hàng
	}

	@RequestMapping("/order/checkout")
	@Transactional
	public String checkout(RedirectAttributes redirectAttributes) {
	    User user = (User) session.get("user");

	    List<Cart> cartItems = cartDAO.findByUserUsername(user.getUsername());
	    if (cartItems.isEmpty()) {
	        redirectAttributes.addFlashAttribute("message", "Giỏ hàng của bạn đang trống!");
	        return "redirect:/cart";
	    }

	    Order order = new Order();
	    order.setId(null); 
	    order.setUser(user);
	    order.setOrderDate(new Date());
	    order.setStatus("Pending");
	    order.setShippingFee(25000.0); 

	    orderDAO.save(order); 

	    for (Cart item : cartItems) {
	        OrderDetail detail = new OrderDetail();
	        detail.setId(null); // ID ngẫu nhiên
	        detail.setOrder(order);
	        detail.setProduct(item.getProduct());
	        detail.setPrice(item.getProduct().getPrice());
	        detail.setQuantity(item.getQuantity());

	        orderDetailDAO.save(detail);
	    }

	    // 3️⃣ Xóa giỏ hàng sau khi thanh toán
	    cartDAO.deleteByUserUsername(user.getUsername());

	    redirectAttributes.addFlashAttribute("message", "Đơn hàng của bạn đã được đặt thành công!");
	    return "redirect:/cart";
	}

	private Sort.Direction resolveSortDirection(String sessionKey, String defaultDirection) {
        Object value = session.get(sessionKey);
        String sortDirStr = (value instanceof String) ? (String) value : defaultDirection;
        return sortDirStr.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    }
	
}
