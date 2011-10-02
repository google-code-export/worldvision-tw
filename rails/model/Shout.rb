# Create your model class
class Shout
  include DataMapper::Resource
  
  property :id, Serial
  property :message, Text
end
