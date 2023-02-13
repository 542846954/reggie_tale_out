package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.entity.AddressBook;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
