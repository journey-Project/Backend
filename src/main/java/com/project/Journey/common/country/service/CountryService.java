package com.project.Journey.common.country.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CountryService {

    @Getter
    private final List<String> countryNames = List.of(
            "국내",
            "일본",
            "미국",
            "프랑스",
            "독일",
            "중국",
            "베트남"
    );
}
