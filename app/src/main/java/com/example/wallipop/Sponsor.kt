package com.example.wallipop

data class Sponsor(
    val accepted_tos: Boolean,
    val bio: String,
    val first_name: String,
    val for_hire: Boolean,
    val id: String,
    val instagram_username: String,
    val last_name: String,
    val links: LinksX,
    val location: Any,
    val name: String,
    val portfolio_url: String,
    val profile_image: ProfileImage,
    val social: Social,
    val total_collections: Int,
    val total_illustrations: Int,
    val total_likes: Int,
    val total_photos: Int,
    val total_promoted_illustrations: Int,
    val total_promoted_photos: Int,
    val twitter_username: String,
    val updated_at: String,
    val username: String
)