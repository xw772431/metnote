package com.metnote.controller.admin.api;

import com.metnote.annotation.DisableOnCondition;
import com.metnote.model.dto.OptionDTO;
import com.metnote.model.dto.OptionSimpleDTO;
import com.metnote.model.entity.Option;
import com.metnote.model.params.OptionParam;
import com.metnote.model.params.OptionQuery;
import com.metnote.service.OptionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Option Controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-20
 */
@RestController
@RequestMapping("/api/admin/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    @ApiOperation("Lists options")
    public List<OptionDTO> listAll() {
        return optionService.listDtos();
    }

    @PostMapping("saving")
    @ApiOperation("Saves options")
    @DisableOnCondition
    public void saveOptions(@Valid @RequestBody List<OptionParam> optionParams) {
        optionService.save(optionParams);
    }

    @GetMapping("map_view")
    @ApiOperation("Lists all options with map view")
    public Map<String, Object> listAllWithMapView() {
        return optionService.listOptions();
    }

    @PostMapping("map_view/keys")
    @ApiOperation("Lists options with map view by keys")
    public Map<String, Object> listAllWithMapView(@RequestBody List<String> keys) {
        return optionService.listOptions(keys);
    }

    @GetMapping("list_view")
    @ApiOperation("Lists all options with list view")
    public Page<OptionSimpleDTO> listAllWithListView(@PageableDefault(sort = "updateTime", direction = DESC) Pageable pageable,
                                                     OptionQuery optionQuery) {
        return optionService.pageDtosBy(pageable, optionQuery);
    }

    @GetMapping("{id:\\d+}")
    @ApiOperation("Gets option detail by id")
    public OptionSimpleDTO getBy(@PathVariable("id") Integer id) {
        Option option = optionService.getById(id);
        return optionService.convertToDto(option);
    }

    @PostMapping
    @ApiOperation("Creates option")
    @DisableOnCondition
    public void createBy(@RequestBody @Valid OptionParam optionParam) {
        optionService.save(optionParam);
    }

    @PutMapping("{optionId:\\d+}")
    @ApiOperation("Updates option")
    @DisableOnCondition
    public void updateBy(@PathVariable("optionId") Integer optionId,
                         @RequestBody @Valid OptionParam optionParam) {
        optionService.update(optionId, optionParam);
    }

    @DeleteMapping("{optionId:\\d+}")
    @ApiOperation("Deletes option")
    @DisableOnCondition
    public void deletePermanently(@PathVariable("optionId") Integer optionId) {
        optionService.removePermanently(optionId);
    }

    @PostMapping("map_view/saving")
    @ApiOperation("Saves options by option map")
    @DisableOnCondition
    public void saveOptionsWithMapView(@RequestBody Map<String, Object> optionMap) {
        optionService.save(optionMap);
    }

}
