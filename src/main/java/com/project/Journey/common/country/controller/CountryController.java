package com.project.Journey.common.country.controller;

import com.project.Journey.common.country.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "공통", description = "국가명 상수 API")
@RestController
@RequestMapping("/api/common/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @Operation(
            summary = "국가 이름 목록(한글로 7개)",
            description = """
                    프론트에서 사용되는 고정 7개 한글 국가명을 배열로 반환
                    """
    )
    @GetMapping("/countries")
    public List<String> listCountries() {
        return countryService.getCountryNames();   // 200 OK
    }
}
