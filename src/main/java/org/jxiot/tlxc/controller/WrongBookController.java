package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.WrongBook;
import org.jxiot.tlxc.service.WrongBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wrongbook")
@CrossOrigin
public class WrongBookController {

    @Autowired
    private WrongBookService wrongBookService;

    @GetMapping
    public ApiResponse<List<WrongBook>> getWrongBooks(@RequestAttribute Integer userId,
                                                      @RequestParam(required = false, defaultValue = "unmastered") String status) {
        List<WrongBook> wrongBooks = wrongBookService.getWrongBooks(userId, status);
        return ApiResponse.success(wrongBooks);
    }

    @PostMapping("/{id}/master")
    public ApiResponse<Void> markAsMastered(@PathVariable Integer id) {
        wrongBookService.markAsMastered(id);
        return ApiResponse.success();
    }
}
