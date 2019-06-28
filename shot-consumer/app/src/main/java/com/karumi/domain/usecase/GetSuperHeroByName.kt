package com.karumi.domain.usecase

import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.SuperHero

class GetSuperHeroByName(private val superHeroesRepository: SuperHeroRepository) {

    operator fun invoke(name: String): SuperHero = superHeroesRepository.getByName(name)
}