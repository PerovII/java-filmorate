package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MpaDto> getAll() {
        return mpaStorage.findAll().stream()
                .map(MpaMapper::mapToMpaDto)
                .collect(Collectors.toList());
    }

    public MpaDto getById(long id) {
        Mpa mpa = mpaStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
        return MpaMapper.mapToMpaDto(mpa);
    }
}