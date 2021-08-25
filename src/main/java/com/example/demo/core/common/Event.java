package com.example.demo.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author fathyaff
 * @date 15/08/21 23.19
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private long unixTimestamp;
}