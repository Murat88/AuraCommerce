# AuraCart - System Context & AI Guidelines

## 1. Project Overview
**AuraCart** is an Enterprise-grade, Multi-Tenant SaaS E-commerce platform built with **Java 21** and **Spring Boot 3.x**. It is designed to be highly scalable, secure, and AI-ready.

## 2. Architectural Pattern: Modular Monolith
The application follows a **Modular Monolith** architecture. It runs as a single Spring Boot application but is strictly partitioned into independent modules.
*   **Host Module**: `application` (The entry point that bundles everything).
*   **Core Module**: `core-shared` (Contains global configs, `BaseEntity`, `TenantContext`, security filters, and shared utilities. NO business logic here).
*   **Domain Modules**: `module-iam`, `module-catalog`, `module-inventory`, `module-sales`, `module-notification`.

**Strict Rule**: Modules **MUST NOT** directly access another module's database tables/entities. Tight coupling is strictly prohibited.

## 3. Database Strategy: Database-per-Tenant
We utilize a highly isolated Multi-Tenant data architecture.
*   **Master DB**: Contains ONLY the `tenants` and `tenant_features` tables. It holds connection metadata.
*   **Tenant DB**: Every tenant (customer) has their own physically isolated database schema containing operational data (Products, Orders, Customers, etc.).
*   **Routing mechanism**: HTTP Requests include tenant identifiers (via Headers or JWT). The `TenantInterceptor` extracts this and sets it in a ThreadLocal `TenantContext`. Spring's `AbstractRoutingDataSource` dynamically routes queries to the correct Tenant DB based on this context.

## 4. Inter-Module Communication
*   **Event-Driven**: Modules communicate via Spring Application Events. For example, `module-sales` fires an `OrderCreatedEvent`, which `module-inventory` listens to.
*   **Outbox Pattern**: To guarantee data consistency, asynchronous tasks must use the `outbox_events` table. Domain operations and their resulting events are saved in the same local database transaction. A separate worker processes the outbox.

## 5. Strict Coding Standards & Constraints
When writing code for this project, AI assistants MUST follow these rules:
1.  **BaseEntity**: All JPA Entities must extend a common `@MappedSuperclass` `BaseEntity` located in `core-shared` (which handles `id`, `created_at`, `updated_at`, etc.).
2.  **JPA Auditing**: Use `@CreatedDate`, `@LastModifiedDate`, and `@CreatedBy` annotations. Do not set these fields manually.
3.  **Tenant Context Handling**: Never hardcode tenant IDs. Always rely on `TenantContext.getTenantId()`.
4.  **Soft Deletes**: Use active/inactive flags (e.g., `is_active`) or status Enums instead of hard `DELETE` queries, unless explicitly requested.
5.  **Audit Logs**: Critical changes should be tracked in the `audit_logs` table.
6.  **AI Readiness**: Recognize that vector embeddings (`pgvector`) are used in the DB (`ai_embeddings`, `preferences_embedding`).

---

## 6. Single Source of Truth: Database Schema (DBML)
Below is the strict database schema that dictates the domain design. Refer to this schema when generating Entities, Repositories, and Services.

```dbml
// ==============================================================================  
// 1. MASTER DB (SaaS YÖNETİM VERİTABANI)  
// ==============================================================================  
Table tenants {  
  id uuid [pk]  
  company_name varchar [note: 'Firma Adı']  
  subdomain varchar [unique, note: 'Örn: magaza.seninsistemin.com']  
  custom_domain varchar [unique, note: 'Örn: [www.harikaayakkabi.com](https://www.harikaayakkabi.com)']  
  db_name varchar [unique, note: 'Bu firmaya özel açılmış veritabanının adı (Örn: tenant_db_001)']  
  is_active boolean  
  created_at timestamp  
}  
  
Table tenant_features {  
  tenant_id uuid [pk]  
  feature_code varchar [pk, note: 'Örn: AI_SEARCH, SMS_NOTIFICATIONS, B2B_PRICING']  
  is_enabled boolean  
  expires_at timestamp [note: 'Deneme süresi veya abonelik bitişi']  
}  
  
Ref: tenant_features.tenant_id > tenants.id  

// ==============================================================================  
// 2. TENANT DB (HER MAĞAZAYA ÖZEL AÇILAN ŞABLON VERİTABANI)  
// ==============================================================================  
  
// ================= ENUMS =================  
Enum product_status { 
  DRAFT 
  ACTIVE 
  ARCHIVED 
}  

Enum variant_status { 
  ACTIVE 
  INACTIVE 
}  

Enum product_type { 
  PHYSICAL 
  DIGITAL 
  SERVICE 
}  

Enum media_type { 
  IMAGE 
  VIDEO 
  MODEL_3D 
}  

Enum event_type { 
  VIEW_PRODUCT 
  VIEW_VARIANT 
  ADD_TO_CART 
  REMOVE_FROM_CART 
  PURCHASE 
  HOVER_LONG 
}  

Enum payment_status { 
  UNPAID 
  AUTHORIZED 
  PAID 
  FAILED 
  REFUNDED 
}  

Enum moderation_status { 
  PENDING 
  APPROVED 
  REJECTED 
}  

Enum sentiment { 
  POSITIVE 
  NEUTRAL 
  NEGATIVE 
}  

Enum notification_type { 
  INFORMATIONAL 
  PROMOTIONAL 
}  

Enum notification_channel { 
  SMS 
  EMAIL 
  PUSH_NOTIFICATION 
}  

Enum notification_status { 
  PENDING 
  SENT 
  FAILED 
  BLOCKED_BY_IYS 
}  

Enum outbox_status { 
  PENDING 
  PUBLISHED 
  FAILED 
}  

Enum inventory_transaction_type { 
  PURCHASE 
  SALE 
  RETURN 
  DAMAGE 
  COUNT_ADJUSTMENT 
}  

Enum return_status { 
  REQUESTED 
  APPROVED 
  ITEM_RECEIVED 
  REFUNDED 
  REJECTED 
}  
  
Enum order_core_state {  
  PENDING [note: 'Ödeme bekleniyor']  
  PROCESSING [note: 'Hazırlanıyor (Stok rezerve edildi)']  
  READY_FOR_PICKUP [note: 'Kargo/Müşteri teslim almayı bekliyor']  
  SHIPPED [note: 'Kargoya verildi']  
  DELIVERED [note: 'Başarıyla teslim edildi']  
  CANCELLED [note: 'İptal edildi']  
  REFUNDED [note: 'İade edildi']  
}  
  
// ================= MICROSERVICES & ASYNC (OUTBOX PATTERN) =================  
Table outbox_events {  
  id uuid [pk]  
  aggregate_type varchar [note: 'Hangi modül? Örn: ORDER, CUSTOMER']  
  aggregate_id uuid [note: 'İşlemi yapılan kaydın IDsi']  
  event_type varchar [note: 'Ne oldu? Örn: ORDER_CREATED']  
  payload jsonb [note: 'Mesajın tam içeriği']  
  status outbox_status  
  created_at timestamp  
  processed_at timestamp  
}  
  
Table audit_logs {  
  id uuid [pk]  
  table_name varchar  
  record_id uuid  
  action varchar [note: 'INSERT, UPDATE, DELETE']  
  old_values jsonb  
  new_values jsonb  
  user_id uuid [note: 'İşlemi yapan personel']  
  created_at timestamp  
}  
  
// ================= IAM (KİMLİK VE YETKİ YÖNETİMİ) =================  
Table users {  
  id uuid [pk]  
  email varchar [unique, note: 'Giriş e-postası']  
  password_hash varchar [note: 'Şifrelenmiş parola']  
  first_name varchar  
  last_name varchar  
  phone varchar  
  is_active boolean [note: 'Personel/Kullanıcı sistemden uzaklaştırma flagi']  
  last_login_at timestamp  
  inserted_at timestamp  
}  
  
Table roles {  
  id uuid [pk]  
  name varchar [note: 'Rol adı (Örn: Kasiyer)']  
  description text  
  is_system boolean [note: 'Sistem rolleri silinemez']  
}  
  
Table permissions {  
  id uuid [pk]  
  module varchar [note: 'Modül adı (CATALOG, SALES)']  
  code varchar [unique, note: 'Uygulama içi yetki kodu (Örn: order:refund)']  
  description text  
}  
  
Table role_permissions {  
  role_id uuid [pk]  
  permission_id uuid [pk]  
}  
  
Table user_roles {  
  user_id uuid [pk]  
  role_id uuid [pk]  
}  
  
// ================= CATALOG, BRANDS & CATEGORIES =================  
Table brands {  
  id uuid [pk]  
  name varchar  
  slug varchar [unique]  
  logo_url varchar  
  description text  
  is_active boolean  
}  
  
Table categories {  
  id uuid [pk]  
  name varchar [note: 'Kategori adı']  
  slug varchar [unique, note: 'SEO URL']  
  parent_id uuid [note: 'Üst kategori referansı']  
  path ltree [note: 'Hiyerarşi yolu (PostgreSQL LTree)']  
  is_active boolean  
}  
  
Table collections {  
  id uuid [pk]  
  name varchar [note: 'Örn: Yaz Sezonu, Yeniler']  
  slug varchar [unique]  
  description text  
  is_active boolean  
}  
  
Table collection_products {  
  collection_id uuid [pk]  
  product_id uuid [pk]  
  sort_order int  
}  
  
Table products {  
  id uuid [pk]  
  category_id uuid  
  brand_id uuid  
  name varchar [note: 'Ürün genel adı']  
  slug varchar [unique, note: 'SEO URL']  
  description text  
  product_type product_type  
  tax_class_id uuid  
  status product_status  
  inserted_at timestamp  
  updated_at timestamp  
}  
  
Table ai_embeddings {  
  id uuid [pk]  
  entity_type varchar [note: 'PRODUCT, CATEGORY, BRAND']  
  entity_id uuid  
  embedding vector [note: 'Semantik vektör']  
  model_version varchar [note: 'Örn: text-embedding-3-small']  
  updated_at timestamp  
}  
  
// ================= PRODUCT VARIANTS & ATTRIBUTES =================  
Table attribute_definitions {  
  id uuid [pk]  
  name varchar [note: 'Örn: Renk, Beden']  
  type varchar [note: 'TEXT, COLOR_HEX, NUMBER']  
}  
  
Table attribute_values {  
  id uuid [pk]  
  attribute_definition_id uuid  
  value varchar [note: 'Örn: Kırmızı, XL, #FF0000']  
}  
  
Table product_variants {  
  id uuid [pk]  
  product_id uuid  
  sku varchar [unique, note: 'Stok Kodu (SKU)']  
  barcode varchar [unique]  
  attributes_json jsonb [note: 'Hızlı okuma ve Elastic için: {"Renk":"Kırmızı"}']  
  weight decimal [note: 'Desi/Ağırlık']  
  dimensions jsonb  
  is_default boolean  
  status variant_status  
}  
  
Table variant_attribute_values {  
  variant_id uuid [pk]  
  attribute_value_id uuid [pk]  
}  
  
Table product_media {  
  id uuid [pk]  
  product_id uuid  
  variant_id uuid [note: 'Sadece bir varyanta aitse doldurulur']  
  media_type media_type  
  url varchar [note: 'CDN erişim linki']  
  alt_text varchar  
  sort_order int  
  is_primary boolean  
}  
  
// ================= INVENTORY & WAREHOUSES =================  
Table warehouses {  
  id uuid [pk]  
  name varchar [note: 'Örn: Merkez Depo, İzmir Mağaza']  
  type varchar [note: 'STORE, WAREHOUSE, DROPSHIP']  
  address text  
  is_active boolean  
}  
  
Table inventory {  
  id uuid [pk]  
  variant_id uuid  
  warehouse_id uuid  
  quantity_on_hand integer [note: 'Eldeki fiziksel stok']  
  quantity_reserved integer [note: 'Satılmış ama çıkışı yapılmamış stok']  
  low_stock_threshold integer  
}  
  
Table inventory_movements {  
  id uuid [pk]  
  variant_id uuid  
  warehouse_id uuid  
  transaction_type inventory_transaction_type  
  quantity_change integer [note: 'Artış (+) veya Azalış (-)']  
  reference_id uuid [note: 'İlgili Order ID veya Return ID']  
  note varchar [note: 'Örn: Sayım düzeltmesi, Hasarlı ürün']  
  created_by uuid [note: 'İşlemi yapan personel']  
  created_at timestamp  
}  
  
// ================= PRICING, TAXES & PROMOTIONS =================  
Table tax_classes {  
  id uuid [pk]  
  name varchar [note: 'Örn: Gıda KDV, Standart KDV']  
  rate decimal [note: 'Örn: 1.0, 20.0']  
}  
  
Table price_lists {  
  id uuid [pk]  
  name varchar [note: 'Fiyat listesi (B2B, Perakende)']  
  currency varchar  
  is_active boolean  
}  
  
Table prices {  
  id uuid [pk]  
  variant_id uuid  
  price_list_id uuid  
  price decimal [note: 'Satış fiyatı']  
  compare_at_price decimal [note: 'Üstü çizili eski fiyat']  
  cost_price decimal [note: 'Maliyet fiyatı']  
  start_date timestamp  
  end_date timestamp  
}  
  
Table promotions {  
  id uuid [pk]  
  name varchar  
  conditions jsonb [note: 'Kurallar (AST)']  
  actions jsonb [note: 'Eylemler (Örn: %20 İndirim)']  
  priority int  
  start_date timestamp  
  end_date timestamp  
  is_active boolean  
}  
  
Table coupon_codes {  
  id uuid [pk]  
  promotion_id uuid  
  code varchar [unique, note: 'Örn: YAZ20']  
  usage_limit integer [note: 'Toplam kaç kez kullanılabilir?']  
  used_count integer  
  customer_limit integer [note: 'Bir müşteri kaç kez kullanabilir?']  
}  
  
// ================= CUSTOMERS, ADDRESSES & EVENTS =================  
Table customers {  
  id uuid [pk]  
  user_id uuid [note: 'Kayıtlıysa Auth referansı, misafirse null']  
  preferences_embedding vector [note: 'AI Zevk Vektörü']  
  phone varchar  
  email varchar  
  inserted_at timestamp  
}  
  
Table customer_addresses {  
  id uuid [pk]  
  customer_id uuid  
  title varchar [note: 'Örn: Ev, İş']  
  country varchar  
  city varchar  
  district varchar  
  neighborhood varchar  
  full_address text  
  coordinates geometry [note: 'PostGIS Point(Lng, Lat)']  
  is_default_billing boolean  
  is_default_shipping boolean  
  ai_routing_hints varchar [note: 'AI kurye ipuçları']  
}  
  
Table customer_events {  
  id uuid [pk]  
  customer_id uuid  
  session_id varchar  
  event_type event_type  
  product_id uuid  
  metadata jsonb  
  created_at timestamp  
}  
  
Table wishlists {  
  customer_id uuid [pk]  
  variant_id uuid [pk]  
  added_at timestamp  
}  
  
// ================= NOTIFICATIONS & IYS CONSENTS =================  
Table customer_consents {  
  id uuid [pk]  
  customer_id uuid  
  channel notification_channel  
  is_opt_in boolean [note: 'Müşteri ticari iletiye izin verdi mi?']  
  iys_reference_id varchar [note: 'İYS sisteminden dönen işlem numarası']  
  consent_date timestamp  
  source varchar [note: 'Örn: Web, Checkout, İYS Mobil']  
}  
  
Table notifications {  
  id uuid [pk]  
  customer_id uuid  
  type notification_type [note: 'INFORMATIONAL veya PROMOTIONAL']  
  channel notification_channel  
  recipient varchar [note: 'Gönderilen telefon no/email (Snapshot)']  
  subject varchar  
  content text  
  status notification_status  
  provider_response text [note: 'Netgsm/İYS hata logu']  
  sent_at timestamp  
}  
  
// ================= ORDERS & STATUSES =================  
Table order_statuses {  
  id uuid [pk]  
  name varchar [note: 'Firmanın statü adı']  
  core_state order_core_state [note: 'Sistem baz durumu']  
  color_code varchar  
  is_default_for_new boolean  
}  
  
Table orders {  
  id uuid [pk]  
  customer_id uuid  
  order_number varchar [unique, note: 'Sipariş No (ORD-1234)']  
  status_id uuid  
    
  shipping_address jsonb [note: 'Teslimat adresi kopyası (Mühürlü)']  
  billing_address jsonb [note: 'Fatura adresi kopyası (Mühürlü)']  
  customer_ip varchar  
    
  subtotal decimal  
  discount_total decimal  
  shipping_cost decimal  
  grand_total decimal  
    
  applied_coupon_id uuid [note: 'Kullanılan kupon kodu referansı']  
    
  inserted_at timestamp  
  updated_at timestamp  
}  
  
Table order_items {  
  id uuid [pk]  
  order_id uuid  
  variant_id uuid  
    
  product_name varchar [note: 'Sipariş anındaki ürün adı (Mühürlü)']  
  variant_attributes jsonb [note: 'Sipariş anındaki özellikler (Mühürlü)']  
  sku varchar [note: 'Sipariş anındaki SKU (Mühürlü)']  
    
  unit_price decimal  
  quantity integer  
  total_price decimal  
}  
  
// ================= PAYMENTS, RETURNS & REFUNDS =================  
Table payments {  
  id uuid [pk]  
  order_id uuid  
  payment_provider varchar  
  transaction_id varchar  
  amount decimal  
  currency varchar  
  status payment_status  
  provider_response jsonb  
  inserted_at timestamp  
}  
  
Table returns {  
  id uuid [pk]  
  order_id uuid  
  customer_id uuid  
  rma_number varchar [unique, note: 'Return Merchandise Authorization numarası']  
  status return_status  
  reason varchar [note: 'Örn: Beden uymadı, Defolu ürün']  
  total_refund_amount decimal  
  created_at timestamp  
  updated_at timestamp  
}  
  
Table return_items {  
  id uuid [pk]  
  return_id uuid  
  order_item_id uuid  
  quantity integer  
  condition varchar [note: 'Örn: Açılmamış, Kullanılmış']  
}  
  
// ================= LOGISTICS & SHIPMENTS (SPLIT FULFILLMENT) =================  
Table shipments {  
  id uuid [pk]  
  order_id uuid  
  courier_company varchar  
  tracking_number varchar  
  tracking_url varchar  
  shipping_label_url varchar  
  status varchar  
  inserted_at timestamp  
}  
  
Table shipment_items {  
  id uuid [pk]  
  shipment_id uuid  
  order_item_id uuid  
  quantity integer  
}  
  
Table shipment_events {  
  id uuid [pk]  
  shipment_id uuid  
  shipment_id uuid  
  event_code varchar  
  description varchar  
  location varchar  
  occurred_at timestamp  
}  
  
// ================= CUSTOMER FEEDBACK (REVIEWS & Q&A) =================  
Table product_reviews {  
  id uuid [pk]  
  product_id uuid  
  customer_id uuid  
  order_item_id uuid  
  rating integer  
  title varchar  
  content text  
  status moderation_status  
  rejection_reason varchar  
  ai_sentiment sentiment  
  ai_summary varchar  
  inserted_at timestamp  
}  
  
Table review_media {  
  id uuid [pk]  
  review_id uuid  
  media_type media_type  
  url varchar  
}  
  
Table product_qa {  
  id uuid [pk]  
  product_id uuid  
  customer_id uuid  
  question text  
  answer text  
  answered_by uuid  
  status moderation_status  
  ai_draft_answer text  
  is_public boolean  
}  
  
// ================= RELATIONS (TENANT İÇİ) =================  
Ref: role_permissions.role_id > roles.id  
Ref: role_permissions.permission_id > permissions.id  
Ref: user_roles.user_id > users.id  
Ref: user_roles.role_id > roles.id  
  
Ref: customers.user_id > users.id  
Ref: customer_addresses.customer_id > customers.id  
Ref: customer_events.customer_id > customers.id  
Ref: customer_events.product_id > products.id  
Ref: customer_consents.customer_id > customers.id  
Ref: notifications.customer_id > customers.id  
Ref: wishlists.customer_id > customers.id  
Ref: wishlists.variant_id > product_variants.id  
  
Ref: categories.parent_id > categories.id  
Ref: products.category_id > categories.id  
Ref: products.brand_id > brands.id  
Ref: products.tax_class_id > tax_classes.id  
  
Ref: collection_products.collection_id > collections.id  
Ref: collection_products.product_id > products.id  
  
Ref: attribute_values.attribute_definition_id > attribute_definitions.id  
Ref: variant_attribute_values.variant_id > product_variants.id  
Ref: variant_attribute_values.attribute_value_id > attribute_values.id  
  
Ref: product_media.product_id > products.id  
Ref: product_media.variant_id > product_variants.id  
Ref: product_variants.product_id > products.id  
  
Ref: inventory.variant_id > product_variants.id  
Ref: inventory.warehouse_id > warehouses.id  
Ref: inventory_movements.variant_id > product_variants.id  
Ref: inventory_movements.warehouse_id > warehouses.id  
  
Ref: prices.variant_id > product_variants.id  
Ref: prices.price_list_id > price_lists.id  
Ref: coupon_codes.promotion_id > promotions.id  
  
Ref: orders.customer_id > customers.id  
Ref: orders.status_id > order_statuses.id  
Ref: orders.applied_coupon_id > coupon_codes.id  
Ref: order_items.order_id > orders.id  
Ref: order_items.variant_id > product_variants.id  
  
Ref: payments.order_id > orders.id  
  
Ref: returns.order_id > orders.id  
Ref: returns.customer_id > customers.id  
Ref: return_items.return_id > returns.id  
Ref: return_items.order_item_id > order_items.id  
  
Ref: shipments.order_id > orders.id  
Ref: shipment_items.shipment_id > shipments.id  
Ref: shipment_items.order_item_id > order_items.id  
Ref: shipment_events.shipment_id > shipments.id  
  
Ref: product_reviews.product_id > products.id  
Ref: product_reviews.customer_id > customers.id  
Ref: product_reviews.order_item_id > order_items.id  
Ref: review_media.review_id > product_reviews.id  
Ref: product_qa.product_id > products.id  
Ref: product_qa.customer_id > customers.id  
Ref: product_qa.answered_by > users.id