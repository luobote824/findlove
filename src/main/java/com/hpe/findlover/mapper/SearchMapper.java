package com.hpe.findlover.mapper;

import com.hpe.findlover.model.Search;
import com.hpe.util.BaseTkMapper;

import java.util.List;


public interface SearchMapper extends BaseTkMapper<Search> {
    List<Search> selectAll();
}
