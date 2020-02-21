package com.karumi.data.repository

import android.util.Log
import com.karumi.domain.model.SuperHero

class SuperHeroRepository {
    companion object {
        private const val BIT_TIME = 1500L
        private const val TAG = "SuperHeroRepository"
    }

    private val superHeroes: List<SuperHero>

    init {
        superHeroes = fakeData()
    }

    fun getAllSuperHeroes(): List<SuperHero> {
        waitABit()
        return superHeroes
    }

    fun getByName(name: String): SuperHero {
        waitABit()
        return superHeroes.first { it.name == name }
    }

    private fun waitABit() {
        try {
            Thread.sleep(BIT_TIME)
        } catch (e: InterruptedException) {
            Log.d(TAG, "wait a bit failed.", e)
        }
    }

    private fun fakeData(): List<SuperHero> {
        return listOf(
            SuperHero(
                name = "Scarlet Witch",
                photo = "https://i.annihil.us/u/prod/marvel/i/mg/9/b0/537bc2375dfb9.jpg",
                isAvenger = false,
                description = """
                    |Scarlet Witch was born at the Wundagore base of the High Evolutionary, she and her twin
                    |brother Pietro were the children of Romani couple Django and Marya Maximoff. The
                    |High Evolutionary supposedly abducted the twins when they were babies and
                    |experimented on them, once he was disgusted with the results, he returned them to
                    |Wundagore, disguised as regular mutants.
                    |""".trimMargin()
            ),
            SuperHero(
                name = "Iron Man",
                photo = "https://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg",
                isAvenger = true,
                description = """
                    |Wounded, captured and forced to build a weapon by his enemies, billionaire
                    |industrialist Tony Stark instead created an advanced suit of armor to save his
                    |life and escape captivity. Now with a new outlook on life, Tony uses his money
                    |and intelligence to make the world a safer, better place as Iron Man.
                    |""".trimMargin()
            ),
            SuperHero(
                name = "Wolverine",
                photo = "https://i.annihil.us/u/prod/marvel/i/mg/9/00/537bcb1133fd7.jpg",
                isAvenger = false,
                description = """Born with super-human senses and the power to heal from almost any wound,
                        |Wolverine was captured by a secret Canadian organization and given an unbreakable
                        |skeleton and claws. Treated like an animal, it took years for him to control
                        |himself. Now, he's a premiere member of both the X-Men and the Avengers.
                        |""".trimMargin()
            ),
            SuperHero(
                name = "Hulk",
                photo = "https://x.annihil.us/u/prod/marvel/i/mg/e/e0/537bafa34baa9.jpg",
                isAvenger = true,
                description = """
                    |Caught in a gamma bomb explosion while trying to save the life of a
                    |teenager, Dr. Bruce Banner was transformed into the incredibly powerful creature called the
                    |Hulk. An all too often misunderstood hero, the angrier the Hulk gets, the
                    |stronger the Hulk gets.
                    |""".trimMargin()
            ),
            SuperHero(
                name = "Storm",
                photo = "https://x.annihil.us/u/prod/marvel/i/mg/c/b0/537bc5f8a8df0.jpg",
                isAvenger = false,
                description = """
                    |Ororo Monroe is the descendant of an ancient line of African priestesses,
                    |all of whom have white hair, blue eyes, and the potential to wield magic.
                    |""".trimMargin()
            ),

            SuperHero(
                name = "Spider-Man",
                photo = "https://x.annihil.us/u/prod/marvel/i/mg/6/60/538cd3628a05e.jpg",
                isAvenger = true,
                description = """
                    |Bitten by a radioactive spider, high school student Peter Parker gained
                    |the speed, strength and powers of a spider. Adopting the name Spider-Man, Peter hoped to start
                    |a career using his new abilities. Taught that with great power comes great
                    |"responsibility, Spidey has vowed to use his powers to help people.
                    |""".trimMargin()
            ),

            SuperHero(
                name = "Ultron",
                photo = "https://i.annihil.us/u/prod/marvel/i/mg/9/a0/537bc7f6d5d23.jpg",
                isAvenger = false,
                description = """
                    |Arguably the greatest and certainly the most horrific creation of
                    |scientific genius Dr. Henry Pym, Ultron is a criminally insane rogue sentient robot dedicated to
                    |conquest and the extermination of humanity.
                    |""".trimMargin()
            ),

            SuperHero(
                name = "BlackPanther",
                photo = "https://i.annihil.us/u/prod/marvel/i/mg/9/03/537ba26276348.jpg",
                isAvenger = false,
                description = """
                    |T'Challa is a brilliant tactician, strategist, scientist, tracker and a
                    |master of all forms of unarmed combat whose unique hybrid fighting style incorporates acrobatics
                    |and aspects of animal mimicry. T'Challa being a royal descendent of a warrior race
                    |is also a master of armed combat, able to use a variety of weapons but prefers
                    |unarmed combat. He is a master planner who always thinks several steps ahead and
                    |will go to extreme measures to achieve his goals and protect the kingdom
                    |of Wakanda.
                    |""".trimMargin()
            ),

            SuperHero(
                name = "Captain America",
                photo = "http://x.annihil.us/u/prod/marvel/i/mg/9/80/537ba5b368b7d.jpg",
                isAvenger = true,
                description = """
                    |Captain America represented the pinnacle of human physical perfection. He
                    |experienced a time when he was augmented to superhuman levels, but generally
                    |performed just below superhuman levels for most of his career. Captain America had a very high
                    |intelligence as well as agility, strength, speed, endurance, and reaction time
                    |superior to any Olympic athlete who ever competed.
                    |""".trimMargin()
            ),

            SuperHero(
                name = "Winter Soldier",
                photo = "https://i.annihil.us/u/prod/marvel/i/mg/7/40/537bca868687c.jpg",
                isAvenger = false,
                description = """
                    |Olympic-class athlete and exceptional acrobat highly skilled in both unarmed
                    |and armed
                    |hand-to-hand combat and extremely accurate marksman. he is fluent in four languages
                    |including German and Russian.""".trimMargin()
            ),

            SuperHero(
                name = "Captain Marvel",
                photo = "https://x.annihil.us/u/prod/marvel/i/mg/6/30/537ba61b764b4.jpg",
                isAvenger = false,
                description = """
                    |Ms. Marvel's current powers include flight, enhanced strength, durability
                    |and the ability to shoot concussive energy bursts from her hands.
                    |""".trimMargin()
            ),

            SuperHero(
                name = "Iron Fist",
                photo = "https://i.annihil.us/u/prod/marvel/i/mg/6/60/537bb1756cd26.jpg",
                isAvenger = false,
                description = """
                    |Through concentration, Iron Fist can harness his spiritual energy, or chi,
                    |to augment his physical and mental capabilities to peak human levels. By focusing his chi
                    |into his hand, he can tap the superhuman energy of Shou-Lao and temporarily
                    |render his fist superhumanly powerful, immune to pain and injury; however, this
                    |process is mentally draining, and he usually needs recovery time before he can
                    |repeat it. Iron Fist can heal himself of any injury or illness and project this
                    |power to heal others.
                    |""".trimMargin()
            )
        )
    }
}