package com.human.ofim.controller;

import org.springframework.ui.Model;

public abstract class AdminBaseController {

    protected String render(Model model, String viewName,
                            String pageTitle, String pageDesc, String activeMenu) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("pageDesc", pageDesc);
        model.addAttribute("activeMenu", activeMenu);
        return viewName;
    }
}