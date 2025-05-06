package com.sparta.bff.feign;

public record EventDto(
    Long eventId,
    String eventTitle,
    String imgUrl
){}
