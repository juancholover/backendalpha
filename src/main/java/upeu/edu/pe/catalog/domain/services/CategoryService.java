package upeu.edu.pe.catalog.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import upeu.edu.pe.catalog.application.dto.CategoryRequestDto;
import upeu.edu.pe.catalog.application.dto.CategoryResponseDto;
import upeu.edu.pe.catalog.application.dto.CategoryUpdateDto;
import upeu.edu.pe.catalog.application.mapper.CategoryMapper;
import upeu.edu.pe.catalog.domain.entities.Category;
import upeu.edu.pe.catalog.domain.repositories.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CategoryMapper categoryMapper;

    public List<CategoryResponseDto> findAll() {
        return categoryRepository.findAllActive()
                .stream()
                .map(categoryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public CategoryResponseDto findById(Long id) {
        Category category = categoryRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + id));
        
        if (!category.getActive()) {
            throw new NotFoundException("Categoría eliminada con ID: " + id);
        }
        
        return categoryMapper.toResponseDto(category);
    }

    @Transactional
    public CategoryResponseDto create(CategoryRequestDto requestDto) {
        if (categoryRepository.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + requestDto.getName());
        }

        Category category = categoryMapper.toEntity(requestDto);
        categoryRepository.persist(category);
        return categoryMapper.toResponseDto(category);
    }

    @Transactional
    public CategoryResponseDto update(Long id, CategoryUpdateDto updateDto) {
        Category category = categoryRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + id));

        if (!category.getActive()) {
            throw new IllegalStateException("No se puede actualizar una categoría eliminada");
        }

        if (updateDto.getName() != null && !updateDto.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(updateDto.getName())) {
                throw new IllegalArgumentException("Ya existe otra categoría con el nombre: " + updateDto.getName());
            }
        }

        categoryMapper.updateEntityFromDto(updateDto, category);
        categoryRepository.persist(category);
        return categoryMapper.toResponseDto(category);
    }

    @Transactional
    public void deleteById(Long id) {
        Category category = categoryRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + id));

        category.setActive(false);
        categoryRepository.persist(category);
    }
}