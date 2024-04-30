package soongsil.kidbean.front.mypage.main.dto

enum class QuizCategory(private val categoryName: String, private val categoryCode: Int) {
    ANIMAL("동물", 0),
    PLANT("식물", 1),
    OBJECT("사물", 2),
    NONE("없음", 3),
    FOOD("음식", 4)
    ;

    fun getCategoryCode(): Float {
        return categoryCode.toFloat()
    }

    fun getCategoryName(): String {
        return categoryName
    }
}