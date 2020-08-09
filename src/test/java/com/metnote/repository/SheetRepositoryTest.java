package com.metnote.repository;

import com.metnote.model.entity.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * Page repository test.
 *
 * @author johnniang
 * @date 3/22/19
 */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class SheetRepositoryTest {

    @Autowired
    SheetRepository sheetRepository;

    @Test
    void listAllTest() {
        List<Sheet> allSheets = sheetRepository.findAll();
        log.debug("{}", allSheets);
    }
}
