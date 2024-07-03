# 아래 db schema를 활용해 플레이/앱 스토어 구독 정보를 저장하는 테이블을 생성하세요.
create table in_app_purchase
(
    id                                bigint unsigned auto_increment
        primary key,
    in_app_purchase_source            char(16)                             not null,
    order_id                          varchar(256)                         not null,
    product_id                        varchar(64)                          not null,
    device_uuid                       varchar(64)                          not null,
    user_id                           varchar(64)                          null,
    purchase_at                       datetime                             not null,
    product_type                      varchar(32)                          not null,
    subscription_payment_status       varchar(8)                           null,
    subscription_expiry_at            datetime                             null,
    subscription_auto_renewing        tinyint(1) default 1                 null,
    subscription_cancel_reason        tinyint unsigned                     null,
    subscription_user_cancellation_at datetime                             null,
    subscription_auto_resume_at       datetime                             null,
    created_at                        datetime                             not null,
    updated_at                        datetime                             not null,
    version                           datetime   default CURRENT_TIMESTAMP not null
)
    charset = utf8mb4;