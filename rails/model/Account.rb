# Create your model class
class Account
  include DataMapper::Resource
  
  property :id, Serial
  property :account, Text
  property :password, Text
  property :role, Text
  property :name, Text
  property :voulenteer_id, Text
  property :voulenteer_type, Text
end
