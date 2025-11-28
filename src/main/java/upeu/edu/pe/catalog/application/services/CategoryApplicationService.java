package upeu.edu.pe.catalog.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.catalog.application.dto.CategoryRequestDto;
import upeu.edu.pe.catalog.application.dto.CategoryResponseDto;
import upeu.edu.pe.catalog.application.dto.CategoryUpdateDto;
import upeu.edu.pe.catalog.application.mapper.CategoryMapper;
import upeu.edu.pe.catalog.domain.entities.Category;
import upeu.edu.pe.catalog.domain.usecases.ActualizarCategoryUseCase;
import upeu.edu.pe.catalog.domain.usecases.BuscarCategoryUseCase;
import upeu.edu.pe.catalog.domain.usecases.CrearCategoryUseCase;
import upeu.edu.pe.catalog.domain.usecases.EliminarCategoryUseCase;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CategoryApplicationService {

    @Inject
    CrearCategoryUseCase crearCategoryUseCase;

    @Inject
    ActualizarCategoryUseCase actualizarCategoryUseCase;

    @Inject
    BuscarCategoryUseCase buscarCategoryUseCase;

    @Inject
    EliminarCategoryUseCase eliminarCategoryUseCase;

    @Inject
    CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponseDto create(CategoryRequestDto requestDto) {
        Category category = crearCategoryUseCase.execute(requestDto.getName(), requestDto.getDescription());
        return categoryMapper.toResponseDto(category);
    }

    @Transactional
    public CategoryResponseDto update(Long id, CategoryUpdateDto updateDto) {
        Category category = actualizarCategoryUseCase.execute(id, updateDto.getName(), updateDto.getDescription());
        return categoryMapper.toResponseDto(category);
    }

    public void delete(Long id) {
        eliminarCategoryUseCase.execute(id);
    }

    public CategoryResponseDto findById(Long id) {
        return categoryMapper.toResponseDto(buscarCategoryUseCase.findById(id));
    }

    public List<CategoryResponseDto> findAll() {
        return buscarCategoryUseCase.findAll().stream()
                .map(categoryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<CategoryResponseDto> search(String query) {
        return buscarCategoryUseCase.search(query).stream()
                .map(categoryMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
