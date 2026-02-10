--
-- PostgreSQL database dump
--

\restrict Y2JZXqJSxyQWlbU0j4WfdtUXmqRu0gEya7Ipm6Rf7dbLkgCXO6j9t9E1GzTkHiS

-- Dumped from database version 16.11 (Debian 16.11-1.pgdg13+1)
-- Dumped by pg_dump version 16.11 (Ubuntu 16.11-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: account_status_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.account_status_enum AS ENUM (
    'ACTIVE',
    'INACTIVE',
    'SUSPENDED',
    'LOCKED',
    'DELETED'
);


--
-- Name: bank_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.bank_enum AS ENUM (
    'VIETCOMBANK',
    'VIETINBANK',
    'BIDV',
    'AGRIBANK',
    'TECHCOMBANK',
    'ACB',
    'MBBANK',
    'SACOMBANK',
    'VPBANK',
    'TPBANK',
    'SHB',
    'OCB',
    'HDBANK',
    'EXIMBANK'
);


--
-- Name: demand_response_status_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.demand_response_status_enum AS ENUM (
    'PENDING',
    'ACCEPTED',
    'EXPIRED',
    'CANCELLED'
);


--
-- Name: discount_type_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.discount_type_enum AS ENUM (
    'PERCENTAGE',
    'FIXED_AMOUNT'
);


--
-- Name: event_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.event_enum AS ENUM (
    'SALE_EVENT',
    'PLACE_PREORDER',
    'CREATE_COUPON'
);


--
-- Name: gender_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.gender_enum AS ENUM (
    'MALE',
    'FEMALE',
    'OTHER',
    'UNSPECIFIED'
);


--
-- Name: order_status_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.order_status_enum AS ENUM (
    'CREATED',
    'COMFIRMED',
    'CANCELLED',
    'WAITING_FOR_SUPPLY',
    'SUPPLY_CONFIRM',
    'OUT_OF_STOCK',
    'PACKING',
    'READY_FOR_PICKUP',
    'SHIPPING',
    'DELIVERY',
    'COMPLETED',
    'RETURN_REQUESTED',
    'RETURNED',
    'REFUNDED'
);


--
-- Name: payment_provider_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.payment_provider_enum AS ENUM (
    'MOMO',
    'ZALOPAY',
    'COD'
);


--
-- Name: payment_type_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.payment_type_enum AS ENUM (
    'E_WALLET',
    'BANK_TRANSFER',
    'COD'
);


--
-- Name: unit_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.unit_enum AS ENUM (
    'KILOGRAM',
    'GRAM',
    'PIECE',
    'DOZEN',
    'LITER',
    'MILLILITER',
    'PACK',
    'BOX',
    'BOTTLE'
);


--
-- Name: verification_status_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.verification_status_enum AS ENUM (
    'UNVERIFIED',
    'PENDING',
    'APPROVED',
    'REJECTED',
    'SUSPENDED'
);


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: buyers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.buyers (
    buyer_id bigint NOT NULL
);


--
-- Name: coupon_policies; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.coupon_policies (
    coupon_policy_id bigint NOT NULL,
    applicable_cate_ids bigint[],
    discount_type public.discount_type_enum,
    discount_val bigint,
    max_discount_amount bigint,
    min_order_value bigint,
    max_uses_per_acc bigint,
    cur_total_uses bigint,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    created_by bigint
);


--
-- Name: demand_responses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.demand_responses (
    demand_resp_id bigint NOT NULL,
    status public.demand_response_status_enum,
    quantity bigint NOT NULL,
    unit public.unit_enum NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone,
    prod_rqst_id bigint NOT NULL,
    provider_id bigint NOT NULL
);


--
-- Name: employees; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.employees (
    emp_id bigint NOT NULL,
    hire_date date,
    user_id bigint
);


--
-- Name: enterprise_stores; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.enterprise_stores (
    store_id bigint NOT NULL,
    store_name character varying(255) NOT NULL,
    store_des character varying(255),
    provider_id bigint NOT NULL
);


--
-- Name: events; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.events (
    event_id bigint,
    event_type public.event_enum,
    cron_exp character varying,
    begin_time timestamp without time zone,
    end_time timestamp without time zone,
    is_active boolean,
    last_trigger timestamp without time zone,
    next_trigger timestamp without time zone,
    event_payload json,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    created_by bigint
);


--
-- Name: orders; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.orders (
    order_id bigint NOT NULL,
    status public.order_status_enum DEFAULT 'CREATED'::public.order_status_enum NOT NULL,
    owned_by bigint,
    confirmed_by bigint,
    packaged_by bigint,
    shipped_by bigint
);


--
-- Name: preorder_policies; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.preorder_policies (
    preorder_policy_id bigint NOT NULL,
    is_active boolean,
    require_payment boolean,
    deposit_percentage bigint,
    min_preorder_day bigint,
    allow_cancel boolean,
    notes character varying,
    cancel_deadline bigint,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    created_by bigint
);


--
-- Name: product_generals; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.product_generals (
    prod_gen_id bigint NOT NULL,
    prod_name character varying(255),
    updated_at timestamp without time zone,
    created_at timestamp without time zone,
    preorder_policy_id bigint,
    enterprise_store_id bigint
);


--
-- Name: providers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.providers (
    provider_id bigint NOT NULL,
    reputation_point bigint DEFAULT 100,
    verification_status public.verification_status_enum DEFAULT 'UNVERIFIED'::public.verification_status_enum NOT NULL,
    bank_id public.bank_enum,
    bank_num character varying(255)
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    user_id bigint NOT NULL,
    email character varying(255) NOT NULL,
    f_name character varying(255) NOT NULL,
    l_name character varying(255) NOT NULL,
    avt_url text,
    dob date,
    p_num character varying(255),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone,
    gender public.gender_enum,
    acc_status public.account_status_enum
);


--
-- Data for Name: buyers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.buyers (buyer_id) FROM stdin;
\.


--
-- Data for Name: coupon_policies; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.coupon_policies (coupon_policy_id, applicable_cate_ids, discount_type, discount_val, max_discount_amount, min_order_value, max_uses_per_acc, cur_total_uses, created_at, updated_at, created_by) FROM stdin;
\.


--
-- Data for Name: demand_responses; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.demand_responses (demand_resp_id, status, quantity, unit, created_at, updated_at, prod_rqst_id, provider_id) FROM stdin;
\.


--
-- Data for Name: employees; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.employees (emp_id, hire_date, user_id) FROM stdin;
\.


--
-- Data for Name: enterprise_stores; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.enterprise_stores (store_id, store_name, store_des, provider_id) FROM stdin;
\.


--
-- Data for Name: events; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.events (event_id, event_type, cron_exp, begin_time, end_time, is_active, last_trigger, next_trigger, event_payload, created_at, updated_at, created_by) FROM stdin;
\.


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.orders (order_id, status, owned_by, confirmed_by, packaged_by, shipped_by) FROM stdin;
\.


--
-- Data for Name: preorder_policies; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.preorder_policies (preorder_policy_id, is_active, require_payment, deposit_percentage, min_preorder_day, allow_cancel, notes, cancel_deadline, created_at, updated_at, created_by) FROM stdin;
\.


--
-- Data for Name: product_generals; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.product_generals (prod_gen_id, prod_name, updated_at, created_at, preorder_policy_id, enterprise_store_id) FROM stdin;
\.


--
-- Data for Name: providers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.providers (provider_id, reputation_point, verification_status, bank_id, bank_num) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (user_id, email, f_name, l_name, avt_url, dob, p_num, created_at, updated_at, gender, acc_status) FROM stdin;
\.


--
-- Name: buyers buyers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.buyers
    ADD CONSTRAINT buyers_pkey PRIMARY KEY (buyer_id);


--
-- Name: coupon_policies coupon_policies_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.coupon_policies
    ADD CONSTRAINT coupon_policies_pkey PRIMARY KEY (coupon_policy_id);


--
-- Name: demand_responses demand_responses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.demand_responses
    ADD CONSTRAINT demand_responses_pkey PRIMARY KEY (demand_resp_id);


--
-- Name: employees employees_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (emp_id);


--
-- Name: enterprise_stores enterprise_stores_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.enterprise_stores
    ADD CONSTRAINT enterprise_stores_pkey PRIMARY KEY (store_id);


--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (order_id);


--
-- Name: preorder_policies preorder_policies_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.preorder_policies
    ADD CONSTRAINT preorder_policies_pkey PRIMARY KEY (preorder_policy_id);


--
-- Name: product_generals product_generals_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_generals
    ADD CONSTRAINT product_generals_pkey PRIMARY KEY (prod_gen_id);


--
-- Name: providers providers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.providers
    ADD CONSTRAINT providers_pkey PRIMARY KEY (provider_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- PostgreSQL database dump complete
--

\unrestrict Y2JZXqJSxyQWlbU0j4WfdtUXmqRu0gEya7Ipm6Rf7dbLkgCXO6j9t9E1GzTkHiS

