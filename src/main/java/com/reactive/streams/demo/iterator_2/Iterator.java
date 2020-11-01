package com.reactive.streams.demo.iterator_2;

/**
 * Observer pattern은 무한한 데이터 스트림에 매력적이다.
 * 그러나 데이터 스트림의 '끝' 을 알리는 기능이 없고, '에러' 전파 메커니즘 또한 포함하고 있지 않다.
 * 또한 consumer가 준비되기 전 producer가 이벤트를 생성하는 것은 그다지 좋은 상황도 아니다.
 *
 * 동기식의 경우 이런 때를 대비해서 Iterator 패턴이 존재한다.
 *
 * Iterator interface는 하나씩 항목을 검사하기 위한 next() method와,
 * 다음 항목의 존재 여부를 위한 hasNext() method를 포함한다. 이는 스트림의 '끝'을 알리 역할을 한다.
 * @param <T>
 */
public interface Iterator<T> {
    T next();
    boolean hasNext();
}
