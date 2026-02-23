--
-- PostgreSQL database dump
--

\restrict hhgPDt5wj1V2FNrSelKqAFX8j2h2FMemo4ittA6kwnydOiIVUMPn0WHakewR4uO

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
-- Name: membership_level_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.membership_level_enum AS ENUM (
    'NEW',
    'MEM',
    'VIP'
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


--
-- Name: set_updated_at(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.set_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: buyers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.buyers (
    buyer_id bigint NOT NULL,
    user_id bigint,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    loyalty_point bigint DEFAULT 50,
    total_orders bigint DEFAULT 0,
    total_spent_amount bigint DEFAULT 0,
    membership_level public.membership_level_enum DEFAULT 'NEW'::public.membership_level_enum
);


--
-- Name: buyers_buyer_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.buyers ALTER COLUMN buyer_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.buyers_buyer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
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
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by bigint
);


--
-- Name: coupon_policies_coupon_policy_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.coupon_policies ALTER COLUMN coupon_policy_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.coupon_policies_coupon_policy_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
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
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    prod_rqst_id bigint NOT NULL,
    provider_id bigint NOT NULL
);


--
-- Name: demand_responses_demand_resp_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.demand_responses ALTER COLUMN demand_resp_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.demand_responses_demand_resp_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: employees; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.employees (
    emp_id bigint NOT NULL,
    hire_date date,
    user_id bigint,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: employees_emp_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.employees ALTER COLUMN emp_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.employees_emp_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: enterprise_stores; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.enterprise_stores (
    store_id bigint NOT NULL,
    store_name character varying(255) NOT NULL,
    store_des character varying(255),
    provider_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: enterprise_stores_store_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.enterprise_stores ALTER COLUMN store_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.enterprise_stores_store_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: events; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.events (
    event_id bigint NOT NULL,
    event_type public.event_enum,
    cron_exp character varying,
    begin_time timestamp without time zone,
    end_time timestamp without time zone,
    is_active boolean,
    last_trigger timestamp without time zone,
    next_trigger timestamp without time zone,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by bigint
);


--
-- Name: events_event_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.events ALTER COLUMN event_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.events_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
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
    shipped_by bigint,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: payment_methods; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.payment_methods (
    payment_method_id bigint NOT NULL,
    payment_type public.payment_type_enum,
    payment_provider public.payment_provider_enum,
    account_num character varying(255),
    is_active boolean,
    is_default boolean,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    buyer_id bigint
);


--
-- Name: payment_methods_payment_method_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.payment_methods ALTER COLUMN payment_method_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.payment_methods_payment_method_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
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
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by bigint
);


--
-- Name: preorder_policies_preorder_policy_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.preorder_policies ALTER COLUMN preorder_policy_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.preorder_policies_preorder_policy_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: product_generals; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.product_generals (
    prod_gen_id bigint NOT NULL,
    prod_name character varying(255),
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    preorder_policy_id bigint,
    enterprise_store_id bigint
);


--
-- Name: product_requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.product_requests (
    prod_request_id bigint NOT NULL,
    unit public.unit_enum,
    quantity bigint,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    prod_gen_id bigint,
    event_id bigint NOT NULL,
    required_after_days bigint NOT NULL
);


--
-- Name: product_requests_prod_request_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.product_requests ALTER COLUMN prod_request_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.product_requests_prod_request_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: providers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.providers (
    provider_id bigint NOT NULL,
    reputation_point bigint DEFAULT 100,
    verification_status public.verification_status_enum DEFAULT 'UNVERIFIED'::public.verification_status_enum NOT NULL,
    bank_id public.bank_enum,
    bank_num character varying(255),
    user_id bigint,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: providers_provider_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.providers ALTER COLUMN provider_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.providers_provider_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: sale_events; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sale_events (
    sale_event_id bigint NOT NULL,
    name character varying(255),
    description character varying(255),
    img text,
    begin_date date,
    end_date date,
    begin_time time without time zone,
    end_time time without time zone,
    is_active boolean,
    display_priority bigint NOT NULL,
    event_id bigint NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


--
-- Name: sale_events_sale_event_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.sale_events ALTER COLUMN sale_event_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.sale_events_sale_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
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
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    gender public.gender_enum,
    acc_status public.account_status_enum
);


--
-- Data for Name: buyers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.buyers (buyer_id, user_id, updated_at, created_at, loyalty_point, total_orders, total_spent_amount, membership_level) FROM stdin;
2	1	2026-02-20 09:55:56.287708	2026-02-20 09:55:10.154153	50	0	0	MEM
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

COPY public.employees (emp_id, hire_date, user_id, updated_at, created_at) FROM stdin;
1	2026-02-20	1	2026-02-20 09:56:29.204894	2026-02-20 09:56:29.204862
\.


--
-- Data for Name: enterprise_stores; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.enterprise_stores (store_id, store_name, store_des, provider_id, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: events; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.events (event_id, event_type, cron_exp, begin_time, end_time, is_active, last_trigger, next_trigger, created_at, updated_at, created_by) FROM stdin;
15	SALE_EVENT	0/5 * * * * ?	\N	\N	\N	\N	\N	2026-02-23 22:08:00.25054	2026-02-23 22:28:47.317785	\N
16	SALE_EVENT	0/7 * * * * ?	\N	\N	\N	\N	\N	2026-02-23 22:08:30.159748	2026-02-23 22:28:47.317785	\N
\.


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.orders (order_id, status, owned_by, confirmed_by, packaged_by, shipped_by, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: payment_methods; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.payment_methods (payment_method_id, payment_type, payment_provider, account_num, is_active, is_default, updated_at, created_at, buyer_id) FROM stdin;
3	E_WALLET	MOMO	0123123123	t	t	2026-02-20 10:04:07.318211	2026-02-20 10:04:07.318163	2
2	COD	COD	\N	f	f	2026-02-20 10:07:34.55429	2026-02-20 10:01:29.511142	2
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
1	Thịt heo	2026-02-22 16:26:32.522787	2026-02-22 16:26:32.522761	\N	\N
\.


--
-- Data for Name: product_requests; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.product_requests (prod_request_id, unit, quantity, created_at, updated_at, prod_gen_id, event_id, required_after_days) FROM stdin;
\.


--
-- Data for Name: providers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.providers (provider_id, reputation_point, verification_status, bank_id, bank_num, user_id, created_at, updated_at) FROM stdin;
35	100	APPROVED	VIETCOMBANK	1231231234	1	2026-02-20 09:42:53.731528	2026-02-20 09:44:41.23633
\.


--
-- Data for Name: sale_events; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.sale_events (sale_event_id, name, description, img, begin_date, end_date, begin_time, end_time, is_active, display_priority, event_id, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (user_id, email, f_name, l_name, avt_url, dob, p_num, created_at, updated_at, gender, acc_status) FROM stdin;
44	khoi41@gmail.com	Tran	Anh Khoi	https://google.com	2004-03-20	0343883254	2026-02-18 20:44:41.889783	2026-02-18 20:44:41.889828	MALE	ACTIVE
1	admin@gmail.com	admin	admin	https://google.com	2004-01-01	0123456789	2026-02-20 08:36:53.860019	2026-02-20 08:36:53.860052	MALE	ACTIVE
\.


--
-- Name: buyers_buyer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.buyers_buyer_id_seq', 2, true);


--
-- Name: coupon_policies_coupon_policy_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.coupon_policies_coupon_policy_id_seq', 1, false);


--
-- Name: demand_responses_demand_resp_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.demand_responses_demand_resp_id_seq', 1, false);


--
-- Name: employees_emp_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.employees_emp_id_seq', 1, true);


--
-- Name: enterprise_stores_store_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.enterprise_stores_store_id_seq', 1, false);


--
-- Name: events_event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.events_event_id_seq', 16, true);


--
-- Name: payment_methods_payment_method_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.payment_methods_payment_method_id_seq', 3, true);


--
-- Name: preorder_policies_preorder_policy_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.preorder_policies_preorder_policy_id_seq', 1, false);


--
-- Name: product_requests_prod_request_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.product_requests_prod_request_id_seq', 2, true);


--
-- Name: providers_provider_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.providers_provider_id_seq', 35, true);


--
-- Name: sale_events_sale_event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.sale_events_sale_event_id_seq', 4, true);


--
-- Name: buyers buyers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.buyers
    ADD CONSTRAINT buyers_pkey PRIMARY KEY (buyer_id);


--
-- Name: buyers buyers_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.buyers
    ADD CONSTRAINT buyers_user_id_key UNIQUE (user_id);


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
-- Name: employees employees_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_user_id_key UNIQUE (user_id);


--
-- Name: enterprise_stores enterprise_stores_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.enterprise_stores
    ADD CONSTRAINT enterprise_stores_pkey PRIMARY KEY (store_id);


--
-- Name: enterprise_stores enterprise_stores_provider_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.enterprise_stores
    ADD CONSTRAINT enterprise_stores_provider_id_key UNIQUE (provider_id);


--
-- Name: events events_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pkey PRIMARY KEY (event_id);


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
-- Name: product_requests product_requests_event_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_requests
    ADD CONSTRAINT product_requests_event_id_key UNIQUE (event_id);


--
-- Name: product_requests product_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_requests
    ADD CONSTRAINT product_requests_pkey PRIMARY KEY (prod_request_id);


--
-- Name: providers providers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.providers
    ADD CONSTRAINT providers_pkey PRIMARY KEY (provider_id);


--
-- Name: providers providers_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.providers
    ADD CONSTRAINT providers_user_id_key UNIQUE (user_id);


--
-- Name: sale_events sale_event_event_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sale_events
    ADD CONSTRAINT sale_event_event_id_key UNIQUE (event_id);


--
-- Name: sale_events sale_event_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sale_events
    ADD CONSTRAINT sale_event_pkey PRIMARY KEY (sale_event_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: buyers trg_buyers_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_buyers_updated_at BEFORE UPDATE ON public.buyers FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: coupon_policies trg_coupon_policies_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_coupon_policies_updated_at BEFORE UPDATE ON public.coupon_policies FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: demand_responses trg_demand_responses_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_demand_responses_updated_at BEFORE UPDATE ON public.demand_responses FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: employees trg_employees_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_employees_updated_at BEFORE UPDATE ON public.employees FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: enterprise_stores trg_enterprise_stores_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_enterprise_stores_updated_at BEFORE UPDATE ON public.enterprise_stores FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: events trg_events_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_events_updated_at BEFORE UPDATE ON public.events FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: orders trg_orders_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_orders_updated_at BEFORE UPDATE ON public.orders FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: payment_methods trg_payment_methods_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_payment_methods_updated_at BEFORE UPDATE ON public.payment_methods FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: preorder_policies trg_preorder_policies_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_preorder_policies_updated_at BEFORE UPDATE ON public.preorder_policies FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: product_generals trg_product_generals_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_product_generals_updated_at BEFORE UPDATE ON public.product_generals FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: providers trg_providers_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_providers_updated_at BEFORE UPDATE ON public.providers FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: users trg_users_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON public.users FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: buyers buyers_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.buyers
    ADD CONSTRAINT buyers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE NOT VALID;


--
-- Name: coupon_policies coupon_policies_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.coupon_policies
    ADD CONSTRAINT coupon_policies_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.employees(emp_id) NOT VALID;


--
-- Name: demand_responses demand_responses_provider_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.demand_responses
    ADD CONSTRAINT demand_responses_provider_id_fkey FOREIGN KEY (provider_id) REFERENCES public.providers(provider_id) ON DELETE CASCADE NOT VALID;


--
-- Name: employees employees_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE NOT VALID;


--
-- Name: enterprise_stores enterprise_stores_provider_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.enterprise_stores
    ADD CONSTRAINT enterprise_stores_provider_id_fkey FOREIGN KEY (provider_id) REFERENCES public.providers(provider_id) ON DELETE CASCADE NOT VALID;


--
-- Name: events events_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(user_id) ON DELETE CASCADE NOT VALID;


--
-- Name: orders orders_confirmed_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_confirmed_by_fkey FOREIGN KEY (confirmed_by) REFERENCES public.employees(emp_id) NOT VALID;


--
-- Name: orders orders_owned_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_owned_by_fkey FOREIGN KEY (owned_by) REFERENCES public.buyers(buyer_id) NOT VALID;


--
-- Name: orders orders_packaged_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_packaged_by_fkey FOREIGN KEY (packaged_by) REFERENCES public.employees(emp_id) NOT VALID;


--
-- Name: orders orders_shipped_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_shipped_by_fkey FOREIGN KEY (shipped_by) REFERENCES public.employees(emp_id) NOT VALID;


--
-- Name: payment_methods payment_methods_buyer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payment_methods
    ADD CONSTRAINT payment_methods_buyer_id_fkey FOREIGN KEY (buyer_id) REFERENCES public.buyers(buyer_id) NOT VALID;


--
-- Name: preorder_policies preorder_policies_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.preorder_policies
    ADD CONSTRAINT preorder_policies_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.employees(emp_id) NOT VALID;


--
-- Name: product_generals product_generals_preorder_policy_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_generals
    ADD CONSTRAINT product_generals_preorder_policy_id_fkey FOREIGN KEY (preorder_policy_id) REFERENCES public.preorder_policies(preorder_policy_id) ON DELETE SET NULL NOT VALID;


--
-- Name: product_requests product_requests_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_requests
    ADD CONSTRAINT product_requests_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.events(event_id) ON UPDATE CASCADE;


--
-- Name: product_requests product_requests_prod_gen_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_requests
    ADD CONSTRAINT product_requests_prod_gen_id_fkey FOREIGN KEY (prod_gen_id) REFERENCES public.product_generals(prod_gen_id) ON DELETE SET NULL;


--
-- Name: providers providers_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.providers
    ADD CONSTRAINT providers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE NOT VALID;


--
-- Name: sale_events sale_event_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sale_events
    ADD CONSTRAINT sale_event_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.events(event_id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict hhgPDt5wj1V2FNrSelKqAFX8j2h2FMemo4ittA6kwnydOiIVUMPn0WHakewR4uO

