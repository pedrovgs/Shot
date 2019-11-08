package com.karumi.domain.usecase

import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.SuperHero

class GetSuperHeroes(private val superHeroesRepository: SuperHeroRepository) {

    operator fun invoke(): List<SuperHero> = superHeroesRepository.getAllSuperHeroes()
}