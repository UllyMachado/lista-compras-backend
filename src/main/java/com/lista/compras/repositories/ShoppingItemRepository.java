package com.lista.compras.repositories;

import com.lista.compras.models.ShoppingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, String> {
}
