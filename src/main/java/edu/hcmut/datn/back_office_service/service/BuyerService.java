package edu.hcmut.datn.back_office_service.service;

import java.util.List;

import edu.hcmut.datn.back_office_service.dao.Buyer;

public interface BuyerService {
    Buyer create(Buyer buyer);

    Buyer read(Long buyerId);

    List<Buyer> readAll(Integer pageNum, Integer pageSize);

    Buyer update(Long buyerId, Buyer buyer);

    void delete (Long buyerId);
}
