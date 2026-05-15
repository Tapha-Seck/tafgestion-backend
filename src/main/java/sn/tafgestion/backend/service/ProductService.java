package sn.tafgestion.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.tafgestion.backend.dto.ProductRequest;
import sn.tafgestion.backend.dto.ProductResponse;
import sn.tafgestion.backend.model.Product;
import sn.tafgestion.backend.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // ── CRÉER ─────────────────────────────────────────────
    @Transactional
    public ProductResponse create(ProductRequest request) {

        // Générer référence auto si non fournie
        String reference = request.getReference();
        if (reference == null || reference.isBlank()) {
            Long maxId = productRepository.findMaxId();
            long nextId = (maxId == null ? 0 : maxId) + 1;
            reference = String.format("PRD-%03d", nextId);
        }

        if (productRepository.existsByReference(reference)) {
            throw new RuntimeException("Référence déjà utilisée : " + reference);
        }

        Product product = Product.builder()
                .reference(reference)
                .name(request.getName())
                .category(request.getCategory())
                .priceHt(request.getPriceHt())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .description(request.getDescription())
                .active(true)
                .build();

        return ProductResponse.fromProduct(productRepository.save(product));
    }

    // ── LISTE PAGINÉE ─────────────────────────────────────
    public Page<ProductResponse> getAll(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return productRepository
                    .findByActiveTrueAndNameContainingIgnoreCase(search, pageable)
                    .map(ProductResponse::fromProduct);
        }
        return productRepository.findByActiveTrue(pageable)
                .map(ProductResponse::fromProduct);
    }

    // ── DÉTAIL ────────────────────────────────────────────
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        return ProductResponse.fromProduct(product);
    }

    // ── MODIFIER ──────────────────────────────────────────
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPriceHt(request.getPriceHt());
        product.setDescription(request.getDescription());

        return ProductResponse.fromProduct(productRepository.save(product));
    }

    // ── MODIFIER STOCK ────────────────────────────────────
    @Transactional
    public ProductResponse updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new RuntimeException("Stock insuffisant");
        }
        product.setStock(newStock);
        return ProductResponse.fromProduct(productRepository.save(product));
    }

    // ── SUPPRIMER (soft delete) ───────────────────────────
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        product.setActive(false);
        productRepository.save(product);
    }

    // ── ALERTES STOCK BAS ─────────────────────────────────
    public List<ProductResponse> getLowStock(Integer threshold) {
        return productRepository
                .findByActiveTrueAndStockLessThan(threshold != null ? threshold : 10)
                .stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
    }
}
