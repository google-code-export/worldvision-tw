require 'sinatra'
require 'dm-core'
require 'rails/model/letter'


# Configure DataMapper to use the App Engine datastore 
DataMapper.setup(:default, "appengine://auto")

# Make sure our template can use <%=h
helpers do
  include Rack::Utils
  alias_method :h, :escape_html
end

get '/voulenteer' do
  # Just list all the shouts
  @letters = Letter.all
  erb :voulenteer_index
end

post '/claim_letter' do
  id = params[:id]
  if (!id.nil?)
    letter = Letter.get(id)
    letter.voulenteer_id = "WV000000"
    letter.voulenteer_name = "郭雯婷"
    letter.claim_date = Date.today
    letter.due_date = Date.today + 7
    letter.status="已領取"
    letter.save
  end
  redirect '/voulenteer'
end

__END__

@@ voulenteer_index
