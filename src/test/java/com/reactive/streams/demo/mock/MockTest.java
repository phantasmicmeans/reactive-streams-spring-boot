package com.reactive.streams.demo.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class MockTest {

    /**
     * given - when - then
     * given (test에서 구체화 하고자 하는 행동을 시작하기 전, 테스트 상태를 설명하는 부)
     * when  (구체화 하고자 하는 행)
     * then  (예상되는 변화에 대한 설명)
     *
     * 기능 : 사용자 주식 트레이드
     *
     * 시나리오 : 트레이드가 마감되기 전에 사용자가 판매를 요청
     *
     * "Given" 나는 MSFT 주식을 100가지고 있다.
     *         그리고 나는 APPL 주식을 150가지고 있다.
     *         그리고 시간은 트레이드가 종료되기 전이다.
     *
     * "When"  나는 MSFT 주식 20을 팔도록 요청했다.
     *
     * "Then"  나는 MSFT 주식 80 가지고 있어야 한다.
     *         그리고 나는 APPL 주식 150을 가지고 있어야 한다.
     *         그리고 MSFT 주식 20이 판매 요청이 실행되었어야 한다.
     */
    @Test
    public void mockTest() {
        MyList listMock = Mockito.mock(MyList.class);
        when(listMock.add(anyString())).thenReturn(false);
    }
}