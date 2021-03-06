package com.spr.controller;

import java.util.List;

import javax.validation.Valid;

import com.spr.exception.EmployeeNotFound;
import com.spr.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spr.exception.ShopNotFound;
import com.spr.model.Shop;
import com.spr.service.ShopService;
import com.spr.validation.ShopValidator;

@Controller
@RequestMapping(value="/shop")
public class ShopController {
	
	@Autowired
	private ShopService shopService;

    @Autowired
    private EmployeeService employeeService;
	
	@Autowired
	private ShopValidator shopValidator;
	
	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(shopValidator);
	}

	@RequestMapping(value="/create", method=RequestMethod.GET)
	public ModelAndView newShopPage() {
		ModelAndView mav = new ModelAndView("shop-new", "shop", new Shop());
		return mav;
	}
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public ModelAndView createNewShop(@ModelAttribute @Valid Shop shop,
			BindingResult result) {
		
		if (result.hasErrors())
			return new ModelAndView("shop-new");
		
		ModelAndView mav = new ModelAndView("index");
		String message = "New shop "+shop.getName()+" was successfully created.";
        mav.addObject("messageSuc", message);

        employeeService.create(shop.getEmployee());
		shopService.create(shop);

		return mav;
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelAndView shopListPage() {
		ModelAndView mav = new ModelAndView("shop-list");
		List<Shop> shopList = shopService.findAll();
		mav.addObject("shopList", shopList);
		return mav;
	}

    @RequestMapping(value="/list-like", method=RequestMethod.POST)
    public ModelAndView shopListLikePage(@RequestParam("name") String name) {
        ModelAndView mav = new ModelAndView("shop-list-like");
        List<Shop> shopList = shopService.searchByName(name);

        mav.addObject("shopList", shopList);
        return mav;
    }

    @RequestMapping(value="/list-like", method=RequestMethod.GET)
    public ModelAndView shopListLikePage() {
        ModelAndView mav = new ModelAndView("shop-list-like");
        return mav;
    }

    @RequestMapping(value="/list-find", method=RequestMethod.POST)
    public ModelAndView shopListFyndPage(@RequestParam("name") String name, @RequestParam("employee.id") Integer emplNumber) {
        ModelAndView mav = new ModelAndView("shop-list-find");
        List<Shop> shopList = shopService.findByNameAndEmplNumber(name, emplNumber);

        mav.addObject("shopList", shopList);
        return mav;
    }

    @RequestMapping(value="/list-find", method=RequestMethod.GET)
    public ModelAndView shopListFyndPage() {
        ModelAndView mav = new ModelAndView("shop-list-find");
        return mav;
    }

	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public ModelAndView editShopPage(@PathVariable Integer id) {
		ModelAndView mav = new ModelAndView("shop-edit");
		Shop shop = shopService.findById(id);
		mav.addObject("shop", shop);
		return mav;
	}
	
	@RequestMapping(value="/edit/{id}", method=RequestMethod.POST)
	public ModelAndView editShop(@ModelAttribute @Valid Shop shop,
			BindingResult result,
			@PathVariable Integer id
			) throws ShopNotFound, EmployeeNotFound {
		
		if (result.hasErrors())
			return new ModelAndView("shop-edit");
		
		ModelAndView mav = new ModelAndView("index");
		mav.addObject("messageSuc", "Shop was successfully updated.");

		shopService.update(shop);
        employeeService.update(shop.getEmployee());

		return mav;
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public ModelAndView deleteShop(@PathVariable Integer id,
			final RedirectAttributes redirectAttributes) throws ShopNotFound {
		
		ModelAndView mav = new ModelAndView("redirect:/index.html");		
		
		Shop shop = shopService.delete(id);
		String message = "The shop "+shop.getName()+" was successfully deleted.";
		
		redirectAttributes.addFlashAttribute("message", message);
		return mav;
	}
	
}
