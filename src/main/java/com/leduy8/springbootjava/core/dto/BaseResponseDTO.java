package com.leduy8.springbootjava.core.dto;

public abstract class BaseResponseDTO<E> {
  public BaseResponseDTO() {}

  public abstract void mapFromEntity(E entity);

  public static <E, D extends BaseResponseDTO<E>> D of(E entity, Class<D> dtoClass) {
    try {
      final D dto = dtoClass.getDeclaredConstructor().newInstance();
      dto.mapFromEntity(entity);
      return dto;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create DTO instance", e);
    }
  }
}
